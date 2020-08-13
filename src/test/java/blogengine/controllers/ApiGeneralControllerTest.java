package blogengine.controllers;

import blogengine.exceptions.authexceptions.NotEnoughPrivilegesException;
import blogengine.models.ModerationStatus;
import blogengine.models.dto.blogdto.ModerationRequest;
import blogengine.models.dto.blogdto.StatisticsDto;
import blogengine.models.dto.blogdto.commentdto.CommentRequest;
import blogengine.models.dto.userdto.EditProfileRequest;
import blogengine.repositories.PostRepository;
import blogengine.repositories.SettingsRepository;
import blogengine.repositories.UserRepository;
import blogengine.services.TagService;
import blogengine.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.mockito.ArgumentMatchers.any;
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
class ApiGeneralControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SettingsRepository settingsRepository;
    @MockBean
    private TagService tagService;
    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String path = "/api";
    @Test
    void getBlogInfo() throws Exception {

        mockMvc.perform(get(path + "/init"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("DevPub")));
    }

    @Test
    @Transactional
    @DisplayName("Moderation - ACCEPT")
    void moderationAccept() throws Exception {

        int postId = 7;
        ModerationRequest request = new ModerationRequest(postId, "accept");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path + "/moderation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Assertions.assertEquals(ModerationStatus.ACCEPTED,
                        postRepository.findById(postId).get().getModerationStatus()));
    }

    @Test
    @Transactional
    @DisplayName("Moderation - DECLINE")
    void moderationDecline() throws Exception {

        int postId = 7;
        ModerationRequest request = new ModerationRequest(postId, "decline");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path + "/moderation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Assertions.assertEquals(ModerationStatus.DECLINE,
                        postRepository.findById(postId).get().getModerationStatus()));
    }

    @Test
    @DisplayName("Get tags")
    void getTags() throws Exception {

        mockMvc.perform(get(path + "/tag"))
                .andExpect(status().isOk());

        verify(tagService, times(1)).findTagsByName(any());
    }

    @Test
    @DisplayName("Get user's statistics")
//    @Sql(value = "/delete-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = "/data-test-statistics.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getUserStatistics() throws Exception {

        loginAsUser(2);

        StatisticsDto dto = new StatisticsDto(2, 2, 0, 35,
                LocalDateTime.of(2020, 3, 12, 0, 0, 1).toEpochSecond(ZoneOffset.UTC));
//        StatisticsDto dto = new StatisticsDto(2, 3, 2, 70,
//                LocalDateTime.of(2020, 1, 1, 0, 0, 1).toInstant(ZoneOffset.UTC).toEpochMilli());
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(get(path + "/statistics/my"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    @DisplayName("Get all statistics")
    void getGeneralStatistics() throws Exception {

        mockMvc.perform(get(path + "/statistics/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewsCount", is(75)));
    }

    @Test
    void calendar() throws Exception {

        mockMvc.perform(get(path + "/calendar")
                .queryParam("year","2020"))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("Add comment - success")
    void addCommentSuccess() throws Exception {

        loginAsUser(2);
        int postId = 1;
        int initialCommentsCount = postRepository.findById(postId).get().getComments().size();
        CommentRequest request = new CommentRequest(null, String.valueOf(postId), "TextXt");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path + "/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Assertions.assertEquals(initialCommentsCount + 1,
                        postRepository.findById(postId).get().getComments().size()));
    }

    @Test
    void editProfileWithPhoto() throws Exception {

        loginAsUser(1);
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "picture.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "picture".getBytes());
        EditProfileRequest request = new EditProfileRequest(0, "Makar", "makar@gmail.com", "123456");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(multipart(path + "/profile/my")
                .file(image)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("Edit profile without removing avatar")
    void editProfileWithoutRemovingAvatar() throws Exception {

        loginAsUser(1);
        EditProfileRequest request = new EditProfileRequest(0, "Makar", "makar@gmail.com", "123456");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path + "/profile/my")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Assertions.assertNotNull(userRepository.findById(1).get().getPhoto()))
                .andExpect(mvcResult -> Assertions.assertEquals("makar@gmail.com",
                        userRepository.findById(1).get().getEmail()));
    }

    @Test
    @Transactional
    @DisplayName("Edit profile with removing avatar")
    void editProfileRemoveAvatar() throws Exception {

        loginAsUser(1);
        EditProfileRequest request = new EditProfileRequest(1, "Makar", "makar@gmail.com", "123456");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path + "/profile/my")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Assertions.assertNull(userRepository.findById(1).get().getPhoto()))
                .andExpect(mvcResult -> Assertions.assertEquals("makar@gmail.com",
                        userRepository.findById(1).get().getEmail()));
    }

    @Test
    @DisplayName("Get settings")
    void getSettings() throws Exception {

        mockMvc.perform(get(path + "/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("STATISTICS_IS_PUBLIC")))
                .andExpect(jsonPath("$", hasKey("POST_PREMODERATION")))
                .andExpect(jsonPath("$", hasKey("MULTIUSER_MODE")));
    }

    @Test
    @DisplayName("Change settings - success")
    void changeSettingsSuccess() throws Exception {

        loginAsModerator();
        Map<String, Boolean> request = Map.of("STATISTICS_IS_PUBLIC", true,
                                            "POST_PREMODERATION", true,
                                            "MULTIUSER_MODE", false);

        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(put(path + "/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Assertions.assertTrue(settingsRepository.findByCode("STATISTICS_IS_PUBLIC").get().getValue()))
                .andExpect(mvcResult -> Assertions.assertTrue(settingsRepository.findByCode("POST_PREMODERATION").get().getValue()))
                .andExpect(mvcResult -> Assertions.assertFalse(settingsRepository.findByCode("MULTIUSER_MODE").get().getValue()));
    }

    @Test
    @DisplayName("Change settings - failure (user is not moderator)")
    void changeSettingsFailure() throws Exception {

        loginAsUser(1);
        Map<String, Boolean> request = Map.of("STATISTICS_IS_PUBLIC", true,
                "POST_PREMODERATION", true,
                "MULTIUSER_MODE", false);

        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(put(path + "/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof NotEnoughPrivilegesException))
                .andExpect(mvcResult -> Assertions.assertEquals("Только модератор может редактировать настройки",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()));
    }

    private void loginAsUser(int id) {
        doReturn(userRepository.findById(id).get()).when(userService).getCurrentUser();
    }

    private void loginAsModerator() {
        loginAsUser(3);
    }
}