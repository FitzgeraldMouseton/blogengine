package blogengine.controllers;

import blogengine.models.CaptchaCode;
import blogengine.models.User;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.authdto.*;
import blogengine.services.AuthService;
import blogengine.services.CaptchaService;
import blogengine.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Sql(value = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/delete-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ApiAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    @MockBean
    private CaptchaService captchaService;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String path = "/api/auth";
    private final String alreadyExistingEmail = "gabriel@gmail.com";
    private final String newUserEmailEmail = "mikhail@gmail.com";
    private final String password = "123456";
    private final String captchaCode = "0000";
    private final String captchaSecret = "0101";
    private final LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC);
    private final LocalDateTime expiredTime = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(3601);

    @Test
    void login() throws Exception {

        LoginRequest loginRequest = new LoginRequest(alreadyExistingEmail, password);
        String json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post(path + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Registration - success")
    void registerSuccess() throws Exception {

        CaptchaCode captcha = new CaptchaCode(1, captchaCode, captchaSecret, time);
        doReturn(captcha).when(captchaService).findBySecretCode(anyString());

        RegisterRequest registerRequest = new RegisterRequest(newUserEmailEmail, "Mikhail Sholokhov",
                password, captchaCode, captchaSecret);

        String json = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post(path + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Registration - failure, captcha is expired")
    void registerFailureCaptchaExpired() throws Exception {

        CaptchaCode captcha = new CaptchaCode(1, captchaCode, captchaSecret, expiredTime);
        doReturn(captcha).when(captchaService).findBySecretCode(anyString());

        RegisterRequest registerRequest = new RegisterRequest(newUserEmailEmail, "Mikhail Sholokhov",
                password, captchaCode, captchaSecret);

        String json = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post(path + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.errors", hasEntry("captchaSecret", "Код капчи устарел")));
    }

    @Test
    @DisplayName("Registration - failure, email is already registered")
    void registerFailureAlreadyRegistered() throws Exception {

        CaptchaCode captcha = new CaptchaCode(1, captchaCode, captchaSecret, time);
        doReturn(captcha).when(captchaService).findBySecretCode(anyString());
        doReturn(mock(User.class)).when(userService).findByEmail(alreadyExistingEmail);
//        when(userService.findByEmail("ernest@gmail.com")).thenReturn(mock(User.class));

        RegisterRequest registerRequest = new RegisterRequest(alreadyExistingEmail, "Gabriel Marques",
                password, captchaCode, captchaSecret);

        String json = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post(path + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.errors", hasEntry("email", "Этот e-mail уже зарегистрирован")));
    }

    @Test
    @DisplayName("Check")
    void check() throws Exception {

        loginAsUser(1);
        AuthenticationResponse response = new AuthenticationResponse();
        UserLoginResponse loginResponse = new UserLoginResponse();
        loginResponse.setId(1);
        response.setUser(loginResponse);
        doReturn(response).when(authService).check();

        mockMvc.perform(get(path + "/check"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id", is(1)));
    }

    @Test
    @DisplayName("Send restore password message")
    void restorePassword() throws Exception {

        RestorePasswordRequest restorePasswordRequest = new RestorePasswordRequest(alreadyExistingEmail);
        String content = objectMapper.writeValueAsString(restorePasswordRequest);

        mockMvc.perform(post(path + "/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());

        verify(authService, times(1)).sendRestorePasswordMessage(alreadyExistingEmail);
    }

    @Test
    void setNewPassword() throws Exception {

        CaptchaCode captcha = new CaptchaCode(1, captchaCode, captchaSecret, time);
        doReturn(captcha).when(captchaService).findBySecretCode(anyString());

        String time = Long.toString(Instant.now().toEpochMilli());
        String encodedTime = RandomStringUtils.randomAlphanumeric(40) + Base64.getEncoder().encodeToString(time.getBytes());
        SetPassRequest request =
                new SetPassRequest("654321", encodedTime, captchaCode, captchaSecret);
        doReturn(new SimpleResponseDto(true)).when(authService).setNewPassword(request);

        String jsonContent = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk());

        verify(authService, times(1)).setNewPassword(request);
    }

    @Test
    void logout() throws Exception {

        mockMvc.perform(get(path + "/logout"))
                .andExpect(status().isOk());
    }

    private void loginAsUser(int id) {
        User user = new User();
        user.setId(id);
        doReturn(user).when(userService).getCurrentUser();
    }

    private MockHttpSession getMockSession() {
        return new MockHttpSession(wac.getServletContext(),
                RequestContextHolder.currentRequestAttributes().getSessionId());
    }
}