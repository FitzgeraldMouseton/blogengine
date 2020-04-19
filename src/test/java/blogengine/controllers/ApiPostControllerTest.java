package blogengine.controllers;

import blogengine.mappers.PostDtoMapper;
import blogengine.models.dto.blogdto.PostDTO;
import blogengine.models.dto.userdto.UserDTO;
import blogengine.repositories.PostRepository;
import blogengine.services.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ApiPostControllerTest {

    @MockBean
    private PostService postService;

    @MockBean
    private PostRepository mockRepository;

    @MockBean
    private PostDtoMapper postDtoMapper;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getPosts() {

    }

    @Test
    void searchPost() {


    }

    @Test
    void getPostById() throws Exception {

//        Post post = new Post(1, true, ModerationStatus.ACCEPTED, new User(), new User(), new Date(),
//                    "Хребты безумия", "Пугающие звуки шогготов", 15);

        PostDTO mockPostDto = new PostDTO(1, "time", new UserDTO(),
                "Хребты безумия", "Пугающие звуки шогготов", 15,
                1, 7, 54, "text", new ArrayList<>(), new String[1]);

        doReturn(mockPostDto).when(postService).findValidPostById(1);

        mockMvc.perform(get("/api/post/{id}", 1))

                //Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                //validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Хребты безумия")));

    }

    @Test
    void searchByDate() {
    }

    @Test
    void searchByTag() {
    }
}