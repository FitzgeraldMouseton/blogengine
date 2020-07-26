package blogengine.services;

import blogengine.mappers.PostDtoMapper;
import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.User;
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.models.dto.blogdto.postdto.PostsInfoResponse;
import blogengine.repositories.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;

@Slf4j
@Sql(value = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/delete-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostDtoMapper postDtoMapper;

    @MockBean
    private PostRepository postRepository;

    private final User user = new User();
    private final LocalDateTime time = LocalDateTime.now();
    private Post activePost = new Post();
    private Post inactivePost = new Post();

    @BeforeEach
    public void init() {
        activePost.setId(1);
        activePost.setActive(true);
        activePost.setModerationStatus(ModerationStatus.ACCEPTED);
        activePost.setUser(user);
        activePost.setTitle("Title");
        activePost.setText("TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText");
        activePost.setTime(time);
        activePost.setViewCount(5);

        inactivePost.setId(2);
        inactivePost.setActive(false);
        inactivePost.setModerationStatus(ModerationStatus.ACCEPTED);
        inactivePost.setUser(user);
        inactivePost.setTitle("Title");
        inactivePost.setText("TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText");
        inactivePost.setTime(time);
        inactivePost.setViewCount(5);

    }

    @Test
    void findPostById() {

        doReturn(Optional.of(activePost)).when(postRepository).findById(1);
        Post returnedPost = postService.findPostById(1);
        Assertions.assertEquals(returnedPost, activePost);
    }

    @Test
    void save() {

    }

    @Test
    void getAllActivePosts() {
    }

    @Test
    void countUserPosts() {
    }

    @Test
    void countUserPostsViews() {
    }

    @Test
    void findFirstPost() {
    }

    @Test
    void findPosts() {

        Post post1 = new Post();
        post1.setTime(LocalDateTime.now(ZoneOffset.UTC));

        Post post2 = new Post();
        post2.setTime(LocalDateTime.now(ZoneOffset.UTC));

        List<Post> posts = List.of(post1, post2);
        Pageable pageable = PageRequest.of(0/10, 10);
        final PostsInfoResponse<PostDto> postDtoPostsInfoResponse = new PostsInfoResponse<>(2, getPostDTOs(posts));
        doReturn(postDtoPostsInfoResponse)
                .when(postRepository).getRecentPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneOffset.UTC), pageable);

        final PostsInfoResponse<PostDto> early = postService.findPosts(0, 10, "early");

        Assertions.assertEquals(early, postDtoPostsInfoResponse);


    }

    @Test
    void findCurrentUserPosts() {
    }

    @Test
    void findAllByQuery() {


    }

    @Test
    void findValidPostById() {
    }

    @Test
    void findPostsByDate() {
    }

    @Test
    void findPostsByTag() {
    }

    @Test
    void postsForModeration() {
    }

    @Test
    void addPost() {
    }

    @Test
    void editPost() {
    }

    @Test
    void addComment() {
    }

    @Test
    void likePost() {
    }

    @Test
    void dislikePost() {
    }

    private List<PostDto> getPostDTOs(final Iterable<Post> posts) {
        List<PostDto> postDtos = new ArrayList<>();
        posts.forEach(post -> {
            PostDto postDTO = postDtoMapper.postToPostDto(post);
            postDtos.add(postDTO);
        });
        return postDtos;
    }
}