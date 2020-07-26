package blogengine.controllers;

import blogengine.models.CaptchaCode;
import blogengine.models.User;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.authdto.LoginRequest;
import blogengine.models.dto.authdto.RegisterRequest;
import blogengine.models.dto.authdto.RestorePasswordRequest;
import blogengine.models.dto.authdto.SetPassRequest;
import blogengine.services.AuthService;
import blogengine.services.CaptchaService;
import blogengine.services.UserService;
import blogengine.util.mail.EmailServiceImpl;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @MockBean
    private CaptchaService captchaService;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthService authService;
    @MockBean
    private EmailServiceImpl emailService;

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

        RegisterRequest registerRequest = new RegisterRequest(alreadyExistingEmail, "Gabriel Markes",
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
    void check() {
        //TODO
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

        //TODO

        SetPassRequest request =
                new SetPassRequest("654321", argThat(arg -> arg.length() == 60), captchaCode, captchaSecret);
        doReturn(new SimpleResponseDto(true)).when(authService).setNewPassword(request);
        String jsonContent = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(path + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent));

        verify(authService, times(1)).setNewPassword(request);
    }

    @Test
    void logout() throws Exception {

        //TODO
    }
}