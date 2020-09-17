package blogengine.controllers;

import blogengine.exceptions.authexceptions.NotEnoughPrivilegesException;
import blogengine.models.Post;
import blogengine.models.User;
import blogengine.models.dto.blogdto.commentdto.CommentRequest;
import blogengine.models.dto.blogdto.postdto.AddPostRequest;
import blogengine.models.dto.blogdto.votedto.VoteRequest;
import blogengine.repositories.PostRepository;
import blogengine.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Sql(value = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/delete-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ApiPostControllerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostRepository postRepository;
    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String path = "/api/post";
    private static AddPostRequest request;
    private static final User user = new User();

    @BeforeAll
    public static void init() {
        user.setId(2);
        user.setModerator(false);
        user.setRegTime(LocalDateTime.now(ZoneOffset.UTC));
        user.setName("Глеб Успенский");
        user.setEmail("gleb@gmail.com");
        user.setPassword("123456");

        request = AddPostRequest.builder()
                .isActive(true)
                .text("TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText")
                .title("Title")
                .timestamp(LocalDateTime.of(2020,4, 3, 0,1).toEpochSecond(ZoneOffset.UTC))
                .tagNames(Collections.emptySet())
                .build();
    }

    @Test
    @DisplayName("Get api/post - get all posts mode Early")
    void getPostsModeEarly() throws Exception {

        mockMvc.perform(get(path)
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("mode", "early"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.count", is(5)))
                .andExpect(jsonPath("$.posts[0].id", is(8)))
                .andExpect(jsonPath("$.posts[4].id", is(1)));
    }

    @Test
    @DisplayName("Get api/post - get all posts mode Recent")
    void getPostsModeRecent() throws Exception {

        mockMvc.perform(get(path)
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("mode", "recent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.count", is(5)))
                .andExpect(jsonPath("$.posts[0].id", is(1)))
                .andExpect(jsonPath("$.posts[4].id", is(8)));
    }

    @Test
    @DisplayName("Get api/post - get all posts mode Popular")
    void getPostsModePopular() throws Exception {

        mockMvc.perform(get(path)
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("mode", "popular"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.count", is(5)))
                .andExpect(jsonPath("$.posts[0].id", is(3)))
                .andExpect(jsonPath("$.posts[4].id", is(2)));
    }

    @Test
    @DisplayName("Get api/post - get all posts mode Best")
    void getPostsModeBest() throws Exception {

        mockMvc.perform(get(path)
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("mode", "best"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.count", is(5)))
                .andExpect(jsonPath("$.posts[0].id", is(3)))
                .andExpect(jsonPath("$.posts[4].id", is(4)));
    }

    @Test
    @DisplayName("Search post by query")
    void searchPost() throws Exception {

        mockMvc.perform(get(path + "/search")
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("query", "Аурелиано"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts[0].id", is(3)));
    }

    @Test
    @DisplayName("Get post - found")
    void getPostByIdFoundSuccess() throws Exception {

        mockMvc.perform(get(path + "/{id}", 2))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.title", is("Нравы Растеряевой улицы")));
    }

    @Test
    @DisplayName("Get post by id - not found")
    void getPostByIdFoundFailure() throws Exception {

        mockMvc.perform(get(path + "/{id}", 10))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Search posts by date")
    void searchByDate() throws Exception {

        mockMvc.perform(get(path + "/byDate")
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("date", "2020-04-01"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count", is(2)));
    }

    @Test
    @DisplayName("Search posts by tag")
    void searchByTag() throws Exception {

        mockMvc.perform(get(path + "/byTag")
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("tag", "МАРКЕС"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts[0].user.id", is(3)))
                .andReturn();
    }

    @Test
    @Transactional
    @DisplayName("Add post when user is moderator - success")
    void addPostModerator() throws Exception {

        loginAsModerator();
        int initialPostsCount = postRepository.findActivePosts().size();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(mvcResult -> Assertions.assertEquals(initialPostsCount + 1, postRepository.findActivePosts().size()));
    }

    @Test
    @DisplayName("Add post when user is not moderator with premoderation - failure")
    void addPostNotModeratorWithPremoderationSuccess() throws Exception {

        setMultiuserModeEnabled();
        setPremoderationModeEnabled();

        int initialPostsCount = postRepository.findActivePosts().size();

        loginAsUser();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(mvcResult -> Assertions.assertEquals(initialPostsCount, postRepository.findActivePosts().size()));
    }

    @Test
    @DisplayName("Add post when user is not moderator without premoderation - success")
    void addPostNotModeratorWithoutPremoderationSuccess() throws Exception {

        setMultiuserModeEnabled();
        int initialPostsCount = postRepository.findActivePosts().size();

        loginAsUser();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(mvcResult -> Assertions.assertEquals(initialPostsCount + 1, postRepository.findActivePosts().size()));
    }

    @Test
    @DisplayName("Add post when user is not moderator - failure because of Multiuser mode is off")
    void addPostNotModeratorFailure() throws Exception {

        loginAsUser();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof NotEnoughPrivilegesException))
                .andExpect(mvcResult -> Assertions.assertEquals("Публиковать посты может только модератор",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()));
    }

    @Test
    @DisplayName("Edit post - success")
    void editPost() throws Exception{

        int postId = 1;

        loginAsUser();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(put(path + "/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> Assertions.assertEquals(request.getText(), postRepository.findById(postId).get().getText()));
    }

    @Test
    @Transactional
    @DisplayName("Like post when it is first like from current user")
    void addLike() throws Exception {

        int postId = 3;
        Post post = postRepository.findById(postId).get();
        int initialVotesCount = post.getVotes().size();

        loginAsUser();

        VoteRequest request = new VoteRequest(postId);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path + "/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> Assertions.assertEquals(initialVotesCount + 1,
                        postRepository.findById(1).get().getVotes().size()));
    }

    @Test
    @Transactional
    @DisplayName("Dislike post")
    void addDislike() throws Exception {

        int postId = 3;
        Post post = postRepository.findById(postId).get();
        int initialVotesCount = post.getVotes().size();

        loginAsUser();

        VoteRequest request = new VoteRequest(postId);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path + "/dislike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> Assertions.assertEquals(initialVotesCount + 1,
                        postRepository.findById(1).get().getVotes().size()));
    }

    @Test
    @DisplayName("Get user's posts - Inactive")
    void getCurrentUserPostsInactive() throws Exception {

        loginAsUser();

        mockMvc.perform(get(path + "/my")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("status", "inactive"))

                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts[0].title", is("Будка")));
    }

    @Test
    @DisplayName("Get user's posts - Pending")
    void getCurrentUserPostsNew() throws Exception {

        loginAsUser();

        mockMvc.perform(get(path + "/my")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("status", "pending"))

                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts[0].title", is("Земной рай")));
    }

    @Test
    @DisplayName("Get user's posts - Published")
    void getCurrentUserPostsAccepted() throws Exception {

        loginAsUser();

        mockMvc.perform(get(path + "/my")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("status", "published"))

                .andExpect(jsonPath("$.count", is(2)));
    }

    @Test
    @DisplayName("Get user's posts - Declined")
    void getCurrentUserPostsDeclined() throws Exception {

        loginAsUser();

        mockMvc.perform(get(path + "/my")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("status", "declined"))

                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts[0].title", is("Крестьянин и крестьянский труд")));
    }

    @Test
    @DisplayName("Get posts for moderation - new posts")
    void getPostsForModerationNew() throws Exception {

        loginAsModerator();

        mockMvc.perform(get(path + "/moderation")
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("status", "new"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts[0].id", is(7)));
    }

    @Test
    @DisplayName("Get posts for moderation - declined posts")
    void getPostsForModerationDecline() throws Exception {

        loginAsModerator();

        mockMvc.perform(get(path + "/moderation")
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("status", "declined"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.posts[0].id", is(5)));
    }

    @Test
    @DisplayName("Get posts for moderation - accepted posts")
    void getPostsForModerationAccept() throws Exception {

        loginAsModerator();

        final MvcResult result = mockMvc.perform(get(path + "/moderation")
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .queryParam("status", "accepted")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.count", is(5)))
                .andReturn();
    }

    // ===================================== Additional methods ======================================

    private void loginAsModerator() {
        user.setModerator(true);
        doReturn(user).when(userService).getCurrentUser();
    }

    private void loginAsUser() {
        user.setModerator(false);
        doReturn(user).when(userService).getCurrentUser();
    }

    private void setMultiuserModeEnabled() {
        jdbcTemplate.execute("update global_settings set value = true where code = 'MULTIUSER_MODE'");
    }

    private void setPremoderationModeEnabled() {
        jdbcTemplate.execute("update global_settings set value = true where code = 'POST_PREMODERATION'");
    }

//    private void loginAsModerator() {
//        LoginRequest loginRequest = new LoginRequest(moderatorLogin, password);
//        authService.login(loginRequest);
//    }
//
//    private void loginAsUser() {
//        LoginRequest loginRequest = new LoginRequest(userLogin, password);
//        authService.login(loginRequest);
//    }
//
//    private void login(String login) {
//        LoginRequest loginRequest = new LoginRequest(login, password);
//        authService.login(loginRequest);
//    }
//
//    private MockHttpSession getMockSession() {
//        return new MockHttpSession(wac.getServletContext(),
//                RequestContextHolder.currentRequestAttributes().getSessionId());
//    }
}