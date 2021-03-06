package blogengine.services;

import blogengine.exceptions.authexceptions.NotEnoughPrivilegesException;
import blogengine.exceptions.blogexeptions.PageNotFoundException;
import blogengine.mappers.PostDtoMapper;
import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.User;
import blogengine.models.Vote;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.ModerationResponse;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private final VoteService voteService;
    private final SettingService settingService;

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

    public PostDto getPost(final int id) {
        User user = userService.getCurrentUser();
        Optional<Post> postOptional = postRepository.findById(id);
        PostDto postDto = null;
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            if (user != null && (user.equals(post.getUser()) || user.isModerator())) {
                postDto = postDtoMapper.singlePostToPostDto(post);
            } else if (post.isActive() && post.getModerationStatus() == ModerationStatus.ACCEPTED && post.getTime().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
                post.setViewCount(post.getViewCount() + 1);
                postRepository.save(post);
                postDto = postDtoMapper.singlePostToPostDto(post);
            }
            return postDto;
        }
        throw new PageNotFoundException();
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
        if (!settingService.isMultiUserEnabled() && !user.isModerator()) {
            throw new NotEnoughPrivilegesException("Публиковать посты может только модератор");
        } else {
            Post post = postDtoMapper.addPostRequestToPost(request);
//            user.addPost(post);
//            post.setUser(user);
//            user.getPosts().add(post);
//            user.getPosts().add(post);
            postRepository.save(post);
            return new SimpleResponseDto(true);
        }
    }

    @Transactional
    public SimpleResponseDto editPost(final int id, final AddPostRequest request) {
        Post post = postDtoMapper.editPostRequestToPost(id, request);
        save(post);
        return new SimpleResponseDto(true);
    }

    @Transactional
    public SimpleResponseDto likePost(final VoteRequest request) {
        User user = userService.getCurrentUser();
        Post post = postRepository.findById(request.getPostId()).orElse(null);
        Vote like = voteService.findLike(post, user);
        if (like != null || post == null) {
            return new SimpleResponseDto(false);
        }
        like = voteService.getLike();
        like.setPost(post);
        like.setUser(user);
        voteService.deleteDislikeIfExists(post, user);
        voteService.save(like);
        return new SimpleResponseDto(true);
    }

    @Transactional
    public SimpleResponseDto dislikePost(final VoteRequest request) {
        User user = userService.getCurrentUser();
        Post post = postRepository.findById(request.getPostId()).orElse(null);
        Vote dislike = voteService.findDislike(post, user);
        if (dislike != null || post == null) {
            return new SimpleResponseDto(false);
        }
        dislike = voteService.getDislike();
        dislike.setUser(user);
        dislike.setPost(post);
        voteService.deleteLikeIfExists(post, user);
        voteService.save(dislike);
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
