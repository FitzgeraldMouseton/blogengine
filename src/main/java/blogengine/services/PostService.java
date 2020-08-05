package blogengine.services;

import blogengine.exceptions.authexceptions.NotEnoughPrivilegesException;
import blogengine.exceptions.authexceptions.UnauthenticatedUserException;
import blogengine.mappers.PostDtoMapper;
import blogengine.models.*;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.ModerationResponse;
import blogengine.models.dto.blogdto.commentdto.CommentRequest;
import blogengine.models.dto.blogdto.commentdto.CommentResponse;
import blogengine.models.dto.blogdto.postdto.AddPostRequest;
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.models.dto.blogdto.postdto.PostsInfoResponse;
import blogengine.models.dto.blogdto.votedto.VoteRequest;
import blogengine.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostDtoMapper postDtoMapper;
    private final PostRepository postRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final VoteService voteService;
    private final SettingService settingService;
    private final TagService tagService;

    //================================= Methods for working with repository =======================

    public Post findPostById(final Integer id) {
        return postRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Не найден пост с id " + id));
    }

    public List<Post> findAllActivePosts() {
        return postRepository.findActivePosts();
    }

    public List<LocalDateTime> findAllDatesInYear(int year) {
        return postRepository.findAllByYear(year);
    }

    public List<Integer> findAllYears() {
        return postRepository.findAllYears();
    }

    public long countActivePosts() {
        return postRepository.countActivePosts();
    }

    public void save(final Post post) {
        postRepository.save(post);
    }

    long countUserPosts(final User user) {
        return postRepository.countActivePostsOfUser(user, ModerationStatus.ACCEPTED);
    }

    long countUserPostsViews(final User user) {
        return postRepository.countUserPostsViews(user);
    }

    long countAllPostsViews() {
        return postRepository.countAllPostsViews();
    }

    Post findFirstPost() {
        return postRepository.findFirstPost().orElse(null);
    }

    Post findFirstPostOfUser(User user) {
        return postRepository.findFirstPostOfUser(user).orElse(null);
    }

    public int countPostsForModeration(final User moderator) {
        return postRepository.countPostsForModeration(moderator);
    }

    //================================= Main logic methods ==========================================

    public PostsInfoResponse<PostDto> findPosts(final int offset, final int limit, final String mode) {

        List<Post> posts;
        long postsCount = countActivePosts();
        Pageable pageable = PageRequest.of(offset/limit, limit);
        switch (mode) {
            case "recent":
                posts = postRepository.getRecentPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneOffset.UTC), pageable);
                break;
            case "early":
                posts = postRepository.getEarlyPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneOffset.UTC), pageable);
                break;
            case "popular":
                posts = postRepository.getPopularPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneOffset.UTC), pageable);
                break;
            case "best":
                posts = postRepository.getBestPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneOffset.UTC), pageable);
                break;
            default:
                throw new IllegalArgumentException("Wrong argument 'mode': " + mode);
        }

        List<PostDto> postDtos = getPostDTOs(posts);
        return new PostsInfoResponse<>(postsCount, postDtos);
    }

    public PostsInfoResponse<PostDto> findCurrentUserPosts(final int offset, final int limit, final String status) {
        User user = userService.getCurrentUser();
        List<Post> posts;
        long postsCount;
        Pageable pageable = PageRequest.of(offset/limit, limit);
        switch (status) {
            case "inactive":
                postsCount = postRepository.countInactivePostsOfUser(user);
                posts = postRepository.getCurrentUserInactivePosts(user, pageable);
                break;
            case "pending":
                postsCount = postRepository.countActivePostsOfUser(user, ModerationStatus.NEW);
                posts = postRepository.getCurrentUserActivePosts(user, ModerationStatus.NEW, pageable);
                break;
            case "declined":
                postsCount = postRepository.countActivePostsOfUser(user, ModerationStatus.DECLINE);
                posts = postRepository.getCurrentUserActivePosts(user, ModerationStatus.DECLINE, pageable);
                break;
            case "published":
                postsCount = postRepository.countActivePostsOfUser(user, ModerationStatus.ACCEPTED);
                posts = postRepository.getCurrentUserActivePosts(user, ModerationStatus.ACCEPTED, pageable);
                break;
            default:
                throw new IllegalArgumentException("Wrong argument 'status': " + status);
        }

        List<PostDto> postDtos = getPostDTOs(posts);
        return new PostsInfoResponse<>(postsCount, postDtos);
    }

    public PostsInfoResponse<ModerationResponse> findPostsForModeration(final int offset, final int limit, final String status) {
        User user = userService.getCurrentUser();
        if(user.isModerator()) {
            long count;
            List<Post> posts;
            Pageable pageable = PageRequest.of(offset/limit, limit);
            switch (status) {
                case "new":
                    count = postRepository.countAllByModerationStatusAndActiveTrue(ModerationStatus.NEW);
                    posts = postRepository.getPostsForModeration(ModerationStatus.NEW, pageable);
                    break;
                case "declined":
                    count = postRepository.countAllByModerationStatusAndActiveTrue(ModerationStatus.DECLINE);
                    posts = postRepository.getPostsForModeration(ModerationStatus.DECLINE, pageable);
                    break;
                case "accepted":
                    count = postRepository.countAllByModerationStatusAndActiveTrue(ModerationStatus.ACCEPTED);
                    posts = postRepository.getPostsForModeration(ModerationStatus.ACCEPTED, pageable);
                    break;
                default:
                    throw new IllegalArgumentException("Wrong argument 'status': " + status);
            }
            List<ModerationResponse> postDtos = getModerationPostDTOs(posts);
            return new PostsInfoResponse<>(count, postDtos);
        }
        return null;
    }

    public PostsInfoResponse<PostDto> findAllByQuery(final int offset, final int limit, final String query) {
        log.info(query);
        Pageable pageable = PageRequest.of(offset/limit, limit);
        List<Post> posts = query == null ? postRepository.getRecentPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable)
                : postRepository.findPostsByQuery(ModerationStatus.ACCEPTED, LocalDateTime.now(), query, pageable);

        List<PostDto> postDtos = getPostDTOs(posts);
        return new PostsInfoResponse<>(posts.size(), postDtos);
    }

    public PostDto findValidPostById(final int id) {
        Optional<Post> postOptional = postRepository.getValidPostById(id, ModerationStatus.ACCEPTED, LocalDateTime.now());
        Post post = null;
        if (postOptional.isPresent()) {
            post = postOptional.get();
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
        return postDtoMapper.singlePostToPostDto(post);
    }

    public PostsInfoResponse<PostDto> findPostsByYear(final int year) {
        LocalDateTime startDate = LocalDateTime.of(year, 1,1,0,0,0);
        LocalDateTime endDate = startDate.plusYears(1);
        List<Post> posts = postRepository.findPostsByDate(ModerationStatus.ACCEPTED, startDate, endDate);
        return new PostsInfoResponse<>(posts.size(), getPostDTOs(posts));
    }

    public PostsInfoResponse<PostDto> findPostsByDate(final int offset, final int limit, final LocalDate date) {
        Pageable pageable = PageRequest.of(offset/limit, limit);
        List<Post> posts = postRepository.findPostsByDate(ModerationStatus.ACCEPTED, date.atStartOfDay(), date.atStartOfDay().plusDays(1), pageable);
        return new PostsInfoResponse<>(posts.size(), getPostDTOs(posts));
    }

    public PostsInfoResponse<PostDto> findPostsByTag(final int offset, final int limit, final String tag) {
        Pageable pageable = PageRequest.of(offset/limit, limit);
        List<Post> posts = postRepository.findAllByTag(ModerationStatus.ACCEPTED, LocalDateTime.now(), tag, pageable);
        return new PostsInfoResponse<>(posts.size(), getPostDTOs(posts));
    }

    @Transactional
    public SimpleResponseDto addPost(final AddPostRequest request) {
        User user = userService.getCurrentUser();
        int initialPostsCount = postRepository.findActivePosts().size();
        log.info("Active posts before: " + initialPostsCount);
        if (!settingService.isMultiUserEnabled() && !user.isModerator()) {
            throw new NotEnoughPrivilegesException("Публиковать посты может только модератор");
        } else {
            Post post = postDtoMapper.addPostRequestToPost(request);
            user.addPost(post);
            initialPostsCount = postRepository.findActivePosts().size();
            log.info("Active posts after: " + initialPostsCount);
            log.info("isActive: " + post.isActive());
            log.info("Status: " + post.getModerationStatus());
            log.info(String.valueOf(post.getTime().isBefore(LocalDateTime.now(ZoneOffset.UTC))));
            log.info("Post time: " + post.getTime());
            log.info("now(): " + LocalDateTime.now(ZoneOffset.UTC));
            Duration duration = Duration.between(post.getTime(), LocalDateTime.now(ZoneOffset.UTC));
            log.info("Duration: " + duration);
            return new SimpleResponseDto(true);
        }
    }

    @Transactional
    public SimpleResponseDto editPost(final int id, final AddPostRequest request) {
        Post post = findPostById(id);
        User user = userService.getCurrentUser();
        post.setUser(user);
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        LocalDateTime requestTime = LocalDateTime.ofEpochSecond(request.getTimestamp(), 0, ZoneOffset.UTC);
        LocalDateTime postTime = requestTime
                .isBefore(LocalDateTime.now(ZoneOffset.UTC)) ? LocalDateTime.now(ZoneOffset.UTC) : requestTime;
        post.setTime(postTime);
        post.setActive(request.isActive());
        if (user.isModerator() || settingService.isPremoderationEnabled()) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        } else {
            post.setModerationStatus(ModerationStatus.NEW);
        }
        Set<Tag> tags = request.getTagNames().stream()
                .map(tagName -> {
                    tagName = tagName.toUpperCase();
                    Tag tag;
                    Optional<Tag> tagOptional = tagService.findTagByName(tagName);
                    if (tagOptional.isEmpty()){
                        tag = new Tag(tagName);
                        tagService.save(tag);
                    } else {
                        tag = tagOptional.get();
                    }
                    return tag;
                }).collect(Collectors.toSet());
        post.addTags(tags);
        save(post);
        return new SimpleResponseDto(true);
    }

    @Transactional
    public CommentResponse addComment(final CommentRequest request) {
        User user = userService.getCurrentUser();
        Comment comment = new Comment();
        Post post = postRepository.findById(Integer.parseInt(request.getPostId())).orElse(null);
        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            Comment parent = commentService.findById(Integer.parseInt(request.getParentId()));
            comment.setParent(parent);
        }
        comment.setText(request.getText());
        comment.setTime(LocalDateTime.now());
        user.addComment(comment);
        post.addComment(comment);
        return new CommentResponse(comment.getId());
    }

    @Transactional
    public SimpleResponseDto likePost(final VoteRequest request) {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UnauthenticatedUserException("Для для того, чтобы оценивать посты, вам нужно авторизоваться");
        }
        Post post = postRepository.findById(request.getPostId()).orElse(null);
        Vote like = voteService.findLike(post, user);
        if (like != null || post == null) {
            return new SimpleResponseDto(false);
        }
        like = voteService.getLike();
        user.addVote(like);
        post.addVote(like);
        voteService.deleteDislikeIfExists(post, user);
        return new SimpleResponseDto(true);
    }

    @Transactional
    public SimpleResponseDto dislikePost(final VoteRequest request) {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UnauthenticatedUserException("Для для того, чтобы оценивать посты, вам нужно авторизоваться");
        }
        Post post = postRepository.findById(request.getPostId()).orElse(null);
        Vote dislike = voteService.findDislike(post, user);
        if (dislike != null || post == null) {
            return new SimpleResponseDto(false);
        }
        dislike = voteService.getDislike();
        user.addVote(dislike);
        post.addVote(dislike);
        voteService.deleteLikeIfExists(post, user);
        return new SimpleResponseDto(true);
    }

    //================================= Additional methods ==========================================

    private List<PostDto> getPostDTOs(final Iterable<Post> posts) {
        List<PostDto> postDtos = new ArrayList<>();
        posts.forEach(post -> {
            PostDto postDTO = postDtoMapper.postToPostDto(post);
            postDtos.add(postDTO);
        });
        return postDtos;
    }

    private List<ModerationResponse> getModerationPostDTOs(final Iterable<Post> posts) {
        List<ModerationResponse> postDtos = new ArrayList<>();
        posts.forEach(post -> {
            ModerationResponse postDTO = postDtoMapper.postToModerationResponse(post);
            postDtos.add(postDTO);
        });
        return postDtos;
    }
}
