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

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@Slf4j
//@Sql(value = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = "/delete-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
    private final LocalDateTime time = LocalDateTime.of(2020, 3, 17, 23, 6);
    private final String text = "text".repeat(25);
    private final Post activePost1 = new Post();
    private final Post activePost2 = new Post();
    private final Post inactivePost = new Post();

    @BeforeEach
    public void init() {
        activePost1.setId(1);
        activePost1.setActive(true);
        activePost1.setModerationStatus(ModerationStatus.ACCEPTED);
        activePost1.setUser(user);
        activePost1.setTitle("Title");
        activePost1.setText(text);
        activePost1.setTime(time);
        activePost1.setViewCount(5);

        activePost2.setId(2);
        activePost2.setActive(true);
        activePost2.setModerationStatus(ModerationStatus.ACCEPTED);
        activePost2.setUser(user);
        activePost2.setTitle("Title");
        activePost2.setText(text);
        activePost2.setTime(time.minusYears(1));
        activePost2.setViewCount(10);

        inactivePost.setId(3);
        inactivePost.setActive(false);
        inactivePost.setModerationStatus(ModerationStatus.NEW);
        inactivePost.setUser(user);
        inactivePost.setTitle("Title");
        inactivePost.setText(text);
        inactivePost.setTime(time);
        inactivePost.setViewCount(0);

    }

    @Test
    void testFindPostById() {

        doReturn(Optional.of(activePost1)).when(postRepository).findById(1);
        Post returnedPost = postService.findPostById(1);
        Assertions.assertEquals(returnedPost, activePost1);
    }

    @Test
    void testSave() {

    }

    @Test
    void testFindAllActivePosts() {

        List<Post> activePosts = List.of(activePost1, activePost2);
        doReturn(activePosts).when(postRepository).findActivePosts();
        List<Post> actualList = postService.findAllActivePosts();
        Assertions.assertEquals(actualList, activePosts);
    }

    @Test
    void testFindAllDatesInYear() {

        List<LocalDateTime> times = List.of(activePost1.getTime(), activePost2.getTime());
        when(postRepository.findAllByYear(2020)).thenReturn(times);
        final List<LocalDateTime> actualList = postService.findAllDatesInYear(2020);
        Assertions.assertEquals(2, actualList.size());
    }

    @Test
    void testFindAllYears() {

        List<Integer> years = List.of(2020);
        when(postRepository.findAllYears()).thenReturn(years);
        final List<Integer> actualList = postService.findAllYears();
        Assertions.assertEquals(1, actualList.size());
    }


    @Test
    void testCountUserPosts() {

        when(postRepository.countActivePostsOfUser(user, ModerationStatus.ACCEPTED)).thenReturn(Long.valueOf(2));
        Long actual = postService.countUserPosts(user);
        Assertions.assertEquals(2, actual);
    }

    @Test
    void testCountUserPostsViews() {

        Long expectedViews = (long) (activePost1.getViewCount() + activePost2.getViewCount());
        when(postRepository.countUserPostsViews(user))
                .thenReturn(expectedViews);
        Long actual = postService.countUserPostsViews(user);
        Assertions.assertEquals(expectedViews, actual);
    }

    @Test
    void testFindFirstPost() {

        Post expectedPost = activePost1;
        when(postRepository.findFirstPost()).thenReturn(Optional.of(expectedPost));
        Post actualPost = postService.findFirstPost();
        Assertions.assertEquals(expectedPost, actualPost);
    }

    @Test
    void testFindPosts() {

//        List<Post> posts = List.of(activePost1, activePost2);
//        Pageable pageable = PageRequest.of(0 / 10, 10);
//        final PostsInfoResponse<PostDto> postDtoPostsInfoResponse = new PostsInfoResponse<>(2, getPostDTOs(posts));
//        doReturn(posts).when(postRepository)
//                .getRecentPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneOffset.UTC), pageable);
//
//        final PostsInfoResponse<PostDto> recent = postService.findPosts(0, 10, "recent");
//
//        Assertions.assertEquals(postDtoPostsInfoResponse, recent);
    }

    @Test
    void findCurrentUserPosts() {

        List<Post> actualPosts = List.of(activePost1, activePost2);

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