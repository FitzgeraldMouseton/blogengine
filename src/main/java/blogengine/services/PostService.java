package blogengine.services;

import blogengine.exceptions.UserNotFoundException;
import blogengine.mappers.PostDtoMapper;
import blogengine.models.*;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.commentdto.CommentRequest;
import blogengine.models.dto.blogdto.commentdto.CommentResponse;
import blogengine.models.dto.blogdto.postdto.AddPostRequest;
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.models.dto.blogdto.postdto.PostsInfo;
import blogengine.models.dto.blogdto.votedto.VoteRequest;
import blogengine.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostDtoMapper postDtoMapper;
    private final PostRepository postRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final VoteService voteService;

    private final static short TITLE_MIN_LENGTH = 10;
    private final static short TEXT_MIN_LENGTH = 100;
    private final static short COMMENT_MIN_LENGTH = 5;

    public Post findPostById(Integer id){
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> getAllPots(){
        return postRepository.findAllBy();
    }

    public void save(Post post){
        postRepository.save(post);
    }

    public Long countUserPosts(User user){
        return postRepository.countAllByUser(user);
    }

    public Long countUserPostsViews(User user){
        return postRepository.countUserPostsViews(user);
    }

    public Post findFirstPost(){
        return postRepository.findFirstByOrderByTime().orElse(null);
    }

    public PostsInfo<PostDto> findPosts(int offset, int limit, String mode) {

        List<Post> posts = null;
        long postsCount = postRepository.countAllByModerationStatusAndTimeBeforeAndActiveTrue(ModerationStatus.ACCEPTED, LocalDateTime.now());
        Pageable pageable = PageRequest.of(offset/limit, limit);
        switch (mode) {
            case "recent":
                posts = postRepository.getRecentPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);
                break;
            case "early":
                posts = postRepository.getEarlyPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);
                break;
            case "popular":
                posts = postRepository.findPopularPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);
                break;
            case "best":
                posts = postRepository.findBestPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);
                break;
        }

        if (posts == null)
            throw new IllegalArgumentException("Wrong argument 'mode': " + mode);

        List<PostDto> postDtos = getPostDTOs(posts);
        return new PostsInfo<>(postsCount, postDtos);
    }

    public PostsInfo<PostDto> findCurrentUserPosts(int offset, int limit, String status){
        User user = userService.getCurrentUser();
        List<Post> posts = null;
        long postsCount = postRepository.countAllByUser(user);
        Pageable pageable = PageRequest.of(offset/limit, limit);
        switch (status) {
            case "inactive":
                posts = postRepository.getCurrentUserInactivePosts(user, pageable);
                break;
            case "pending":
                posts = postRepository.getCurrentUserActivePosts(user, ModerationStatus.NEW, pageable);
                break;
            case "declined":
                posts = postRepository.getCurrentUserActivePosts(user, ModerationStatus.DECLINE, pageable);
                break;
            case "published":
                posts = postRepository.getCurrentUserActivePosts(user, ModerationStatus.ACCEPTED, pageable);
                break;
        }

        if (posts == null)
            throw new IllegalArgumentException("Wrong argument 'status': " + status);

        List<PostDto> postDtos = getPostDTOs(posts);
        return new PostsInfo<>(postsCount, postDtos);
    }

    public PostsInfo<PostDto> findAllByQuery(int offset, int limit, String query){

        Pageable pageable = PageRequest.of(offset/limit, limit);
        List<Post> posts = postRepository.findPostsByQuery(ModerationStatus.ACCEPTED, LocalDateTime.now(), query, pageable);
        List<PostDto> postDtos = getPostDTOs(posts);
        return new PostsInfo<>(posts.size(), postDtos);
    }

    public PostDto findValidPostById(int id) throws UserNotFoundException {
        Optional<Post> postOptional = postRepository.getValidPostById(id, ModerationStatus.ACCEPTED, LocalDateTime.now());
        if (postOptional.isEmpty())
            throw new UserNotFoundException(String.format("Пост с id = %d не найден", id));
        Post post = postOptional.get();
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        return postDtoMapper.singlePostToPostDto(post);
    }

    public PostsInfo<PostDto> findPostsByDate(int offset, int limit, LocalDate date) {
        Pageable pageable = PageRequest.of(offset/limit, limit);
        List<Post> posts = postRepository.findPostsByDate(ModerationStatus.ACCEPTED, date.atStartOfDay(), date.atStartOfDay().plusDays(1), pageable);
        return new PostsInfo<>(posts.size(), getPostDTOs(posts));
    }

    public PostsInfo<PostDto> findPostsByTag(int offset, int limit, String tag) {
        Pageable pageable = PageRequest.of(offset/limit, limit);
        List<Post> posts = postRepository.findAllByTag(ModerationStatus.ACCEPTED, LocalDateTime.now(), tag, pageable);
        return new PostsInfo<>(posts.size(), getPostDTOs(posts));
    }

    public PostsInfo<PostDto> postsForModeration(int offset, int limit, String status){
        User moderator = userService.getCurrentUser();
        long count = postRepository.countAllByModeratorAndActiveTrue(moderator);
        List<Post> posts = null;
        Pageable pageable = PageRequest.of(offset/limit, limit);
        switch (status) {
            case "new":
                posts = postRepository.getPostsForModeration(moderator, ModerationStatus.NEW, pageable);
                break;
            case "declined":
                posts = postRepository.getPostsForModeration(moderator, ModerationStatus.DECLINE, pageable);
                break;
            case "accepted":
                posts = postRepository.getPostsForModeration(moderator, ModerationStatus.ACCEPTED, pageable);
                break;
        }

        if (posts == null)
            throw new IllegalArgumentException("Wrong argument 'status': " + status);

        List<PostDto> postDtos = getPostDTOs(posts);
        return new PostsInfo<>(count, postDtos);
    }

    public SimpleResponseDto addPost(AddPostRequest request){
        User user = userService.getCurrentUser();
        User moderator = userService.getModerator();
        Post post = new Post();
        postDtoMapper.addPostRequestToPost(request, post);
        checkPostParameters(post.getTitle(), post.getText(), post.getTime());
        post.setModerationStatus(ModerationStatus.NEW);
        post.setUser(user);
        post.setModerator(moderator);
        postRepository.save(post);
        return new SimpleResponseDto(true);
    }

    public SimpleResponseDto editPost(int id, AddPostRequest request) {
        User currentUser = userService.getCurrentUser();
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()){
            throw new NoSuchElementException("Не найден пост с id " + id);
        }
        Post post = postOptional.get();
        postDtoMapper.addPostRequestToPost(request, post);
        checkPostParameters(post.getTitle(), post.getText(), post.getTime());
//        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
//        if (post.getTime().isBefore(currentTime)){
//            post.setTime(currentTime);
//        }
        if (!currentUser.isModerator()){
            post.setModerationStatus(ModerationStatus.NEW);
        }
        postRepository.save(post);
        return new SimpleResponseDto(true);
    }

    public CommentResponse addComment(CommentRequest request){
        if (request.getText().isEmpty() || request.getText().length() < COMMENT_MIN_LENGTH)
            throw new IllegalArgumentException("Текст комментария не задан или слишком короткий");
        if (request.getParentId() == null || request.getPostId() == null)
            throw new IllegalArgumentException("Неверный параметр");
        Comment comment = new Comment();
        Post post = postRepository.findById(Integer.parseInt(request.getPostId())).orElse(null);
        comment.setPost(post);
        Comment parent = commentService.findById(Integer.parseInt(request.getParentId()));
        comment.setComment(parent);
        comment.setText(request.getText());
        comment.setUser(userService.getCurrentUser());
        comment.setTime(LocalDateTime.now());
        commentService.save(comment);
        return new CommentResponse(comment.getId());
    }

    @Transactional
    public SimpleResponseDto likePost(VoteRequest request){

        User user = userService.getCurrentUser();
        Post post = postRepository.findById(request.getPostId()).orElse(null);
        Vote vote = voteService.findLike(post, user);
        if (vote != null || post == null){
            return new SimpleResponseDto(false);
        }
        voteService.setLike(post, user);
        voteService.deleteDislikeIfExists(post, user);
        return new SimpleResponseDto(true);
    }

    @Transactional
    public SimpleResponseDto dislikePost(VoteRequest request){

        User user = userService.getCurrentUser();
        Post post = postRepository.findById(request.getPostId()).orElse(null);
        Vote vote = voteService.findDislike(post, user);
        if (vote != null || post == null){
            return new SimpleResponseDto(false);
        }
        voteService.setDislike(post, user);
        voteService.deleteLikeIfExists(post, user);
        return new SimpleResponseDto(true);
    }

    private List<PostDto> getPostDTOs(Iterable<Post> posts){
        List<PostDto> postDtos = new ArrayList<>();
        posts.forEach(post -> {
            PostDto postDTO = postDtoMapper.postToPostDto(post);
            postDtos.add(postDTO);
        });
        return postDtos;
    }

    private void checkPostParameters(String title, String text, LocalDateTime time){
        if (title == null || title.length() < TITLE_MIN_LENGTH) {
            throw new IllegalArgumentException("Заголовок не установлен");
        }
        if (text == null || text.length() < TEXT_MIN_LENGTH) {
            throw new IllegalArgumentException("Текст публикации слишком короткий");
        }
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if (time.isBefore(currentTime)){
            throw new IllegalArgumentException("Неправильная дата");
        }
    }
}
