package blogengine.services;

import blogengine.mappers.PostDtoMapper;
import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.User;
import blogengine.models.dto.blogdto.PostDTO;
import blogengine.repositories.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc

class PostServiceTest {

    @InjectMocks
    @Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private PostDtoMapper postDtoMapper;

    @Test
    void findPostById() {
        Post mockPost = new Post(1, true, ModerationStatus.ACCEPTED, new User(), new User(), LocalDateTime.now(),
                "Хребты безумия", "Пугающие звуки шогготов", 15);
        doReturn(Optional.of(mockPost)).when(postRepository).findById(1);

        Post post = postService.findPostById(1);
        Assertions.assertSame(mockPost, post);
    }

    @Test
    void getAllPots() {
    }

    @Test
    void findPosts() {
    }

    @Test
    void findAllByQuery() {
    }

    @Test
    void findValidPostById() {

        LocalDateTime date = LocalDateTime.now().plusDays(300);

        Post mockPost = new Post(1, true, ModerationStatus.ACCEPTED, new User(), new User(), date,
                    "Хребты безумия", "Пугающие звуки шогготов", 15);

        PostDTO mockDto = postDtoMapper.singlePostToPostDto(mockPost);

        doReturn(Optional.of(mockPost)).when(postRepository)
                .findValidPostById(1, ModerationStatus.ACCEPTED, LocalDateTime.now());
        PostDTO returnedPost = postService.findValidPostById(1);

        //assertNotNull(returnedPost, "Post was found");
        Assertions.assertSame(returnedPost, mockDto, "Posts should be the same");
    }

    @Test
    void findPostsByDate() {

    }

    @Test
    void findPostsByTag() {
    }

    @Test
    void getBlogStatistics() {
    }

    @Test
    void calendar() {
    }
}