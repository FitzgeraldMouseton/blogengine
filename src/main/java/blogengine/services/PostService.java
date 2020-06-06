package blogengine.services;

import blogengine.exceptions.authexceptions.UserNotFoundException;
import blogengine.mappers.PostDtoMapper;
import blogengine.models.*;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.ModerationResponse;
import blogengine.models.dto.blogdto.commentdto.CommentRequest;
import blogengine.models.dto.blogdto.commentdto.CommentResponse;
import blogengine.models.dto.blogdto.postdto.AddPostRequest;
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.models.dto.blogdto.postdto.PostsInfo;
import blogengine.models.dto.blogdto.votedto.VoteRequest;
import blogengine.repositories.PostRepository;
import blogengine.util.RequestChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final RequestChecker requestChecker;

    //================================= Methods for working with repository =======================

    Post findPostById(Integer id){
        return postRepository.findById(id).orElse(null);
    }

    List<Post> getAllPots(){
        return postRepository.findAllBy();
    }

    public void save(Post post){
        postRepository.save(post);
    }

    Long countUserPosts(User user){
        return postRepository.countAllByUser(user);
    }

    Long countUserPostsViews(User user){
        return postRepository.countUserPostsViews(user);
    }

    Post findFirstPost(){
        return postRepository.findFirstByOrderByTime().orElse(null);
    }

    public int countPostsForModeration(User moderator) {
        return postRepository.countPostsForModeration(moderator);
    }

    //================================= Main logic methods ==========================================

    public PostsInfo<PostDto> findPosts(int offset, int limit, String mode) {

        List<Post> posts;
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
            default:
                throw new IllegalArgumentException("Wrong argument 'mode': " + mode);
        }

        List<PostDto> postDtos = getPostDTOs(posts);
        return new PostsInfo<>(postsCount, postDtos);
    }

    public PostsInfo<PostDto> findCurrentUserPosts(int offset, int limit, String status){
        User user = userService.getCurrentUser();
        List<Post> posts;
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
            default:
                throw new IllegalArgumentException("Wrong argument 'status': " + status);
        }

        List<PostDto> postDtos = getPostDTOs(posts);
        return new PostsInfo<>(postsCount, postDtos);
    }

    public PostsInfo<ModerationResponse> postsForModeration(int offset, int limit, String status){
        User user = userService.getCurrentUser();
        if(user.isModerator()){
            long count = postRepository.countAllByModeratorAndActiveTrue(user);
            List<Post> posts;
            Pageable pageable = PageRequest.of(offset/limit, limit);
            switch (status) {
                case "new":
                    posts = postRepository.getPostsForModeration(user, ModerationStatus.NEW, pageable);
                    break;
                case "declined":
                    posts = postRepository.getPostsForModeration(user, ModerationStatus.DECLINE, pageable);
                    break;
                case "accepted":
                    posts = postRepository.getPostsForModeration(user, ModerationStatus.ACCEPTED, pageable);
                    break;
                default:
                    throw new IllegalArgumentException("Wrong argument 'status': " + status);
            }
            List<ModerationResponse> postDtos = getModerationPostDTOs(posts);
            return new PostsInfo<>(count, postDtos);
        }
        return null;
    }

    public PostsInfo<PostDto> findAllByQuery(int offset, int limit, String query){

        Pageable pageable = PageRequest.of(offset/limit, limit);
        List<Post> posts = postRepository.findPostsByQuery(ModerationStatus.ACCEPTED, LocalDateTime.now(), query, pageable);
        List<PostDto> postDtos = getPostDTOs(posts);
        return new PostsInfo<>(posts.size(), postDtos);
    }

    public PostDto findValidPostById(int id) {
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

    public SimpleResponseDto addPost(AddPostRequest request){
        User user = userService.getCurrentUser();
        User moderator = userService.getModerator();
        Post post = new Post();
        postDtoMapper.addPostRequestToPost(request, post);
        requestChecker.checkPostParameters(post.getTitle(), post.getText(), post.getTime());
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
        requestChecker.checkPostParameters(post.getTitle(), post.getText(), post.getTime());
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
        requestChecker.checkCommentRequest(request);
        Comment comment = new Comment();
        Post post = postRepository.findById(Integer.parseInt(request.getPostId())).orElse(null);
        comment.setPost(post);
        if (request.getParentId() != null && !request.getParentId().isEmpty()){
            Comment parent = commentService.findById(Integer.parseInt(request.getParentId()));
            comment.setComment(parent);
        }
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

    //================================= Additional methods ==========================================

    private List<PostDto> getPostDTOs(Iterable<Post> posts){
        List<PostDto> postDtos = new ArrayList<>();
        posts.forEach(post -> {
            PostDto postDTO = postDtoMapper.postToPostDto(post);
            postDtos.add(postDTO);
        });
        return postDtos;
    }

    private List<ModerationResponse> getModerationPostDTOs(Iterable<Post> posts){
        List<ModerationResponse> postDtos = new ArrayList<>();
        posts.forEach(post -> {
            ModerationResponse postDTO = postDtoMapper.postToModerationResponse(post);
            postDtos.add(postDTO);
        });
        return postDtos;
    }
}
