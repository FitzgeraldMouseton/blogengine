package blogengine.controllers;

import blogengine.mappers.PostDtoMapper;
import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.User;
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ApiPostControllerTest {

    @MockBean
    private PostService postService;
    @Autowired
    private PostDtoMapper postDtoMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Get api/post - get all posts")
    void getPosts() {

    }

    @Test
    void searchPost() {
    }

//    @Test
//    @DisplayName("Get /api/post/id - found")
//    void getPostByIdFound() throws Exception {
//        Post mockPost = new Post(true, ModerationStatus.ACCEPTED, new User(), new User(), LocalDateTime.now(), "Call of Cthulu", "Scary sounds", 56);
//        mockPost.setId(1);
//        log.info(mockPost.toString());
//        PostDto postDto = postDtoMapper.singlePostToPostDto(mockPost);
//        log.info(String.valueOf(postDto == null));
//        doReturn(postDto).when(postService).findValidPostById(1);
//
//        mockMvc.perform(get("/api/post/{id}", 1))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//
//                .andExpect(jsonPath("$.title", is("Call of Cthulu")));
//    }

    @Test
    void searchByDate() {
    }

    @Test
    void searchByTag() {
    }

    @Test
    void addPost() {
    }

    @Test
    void editPost() {
    }

    @Test
    void addLike() {
    }

    @Test
    void dislikePost() {
    }

    @Test
    void getCurrentUserPosts() {
    }

    @Test
    void getPostsForModeration() {
    }
}