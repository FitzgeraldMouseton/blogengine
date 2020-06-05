package blogengine.services;

import blogengine.mappers.UserDtoMapper;
import blogengine.models.CaptchaCode;
import blogengine.models.User;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.authdto.*;
import blogengine.util.RequestChecker;
import blogengine.util.SessionStorage;
import blogengine.util.mail.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final EmailServiceImpl emailService;
    private final CaptchaService captchaService;
    private final SessionStorage sessionStorage;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RequestChecker requestChecker;

    @Transactional
    public AuthenticationResponse login(LoginRequest loginRequest) {
        AuthenticationResponse loginResponse;
        User user = userService.findByEmail(loginRequest.getEmail());
        requestChecker.checkLoginRequest(user, loginRequest);
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Integer userId = user.getId();
        sessionStorage.getSessions().put(sessionId, userId);
        UserLoginDto loginDto = userDtoMapper.userToLoginDto(user);
        loginResponse = new AuthenticationResponse(true, loginDto);
        return loginResponse;
    }

    @Transactional
    public SimpleResponseDto register(RegisterRequest registerRequest) {
        CaptchaCode captcha = captchaService.findBySecretCode(registerRequest.getCaptchaSecret());
        requestChecker.checkRegistrationRequest(registerRequest, captcha);
        User user = userDtoMapper.registerDtoToUser(registerRequest);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userService.save(user);
        return new SimpleResponseDto(true);
    }

    public AuthenticationResponse check() {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (!sessionStorage.getSessions().values().isEmpty()) {
            int userId = sessionStorage.getSessions().get(sessionId);
            User user = userService.findById(userId);
            UserLoginDto userDTO = userDtoMapper.userToLoginDto(user);
            return new AuthenticationResponse(true, userDTO);
        }
        return null;
    }

    @Transactional
    public SimpleResponseDto sendRestorePasswordMessage(String email) {
        User user = userService.findByEmail(email);
        if (user == null)
            return new SimpleResponseDto(false);
        String code = createPasswordRestoreCode();
        user.setCode(code);
        userService.save(user);
        String message = "Для восстановления пароля перейдите по ссылке http://localhost:8080/login/change-password/" + code;
        emailService.send(email, "Васстановление пароля", message);
        return new SimpleResponseDto(true);
    }

    public SimpleResponseDto setNewPassword(SetPassRequest request) {
        String code = request.getCode();
        User user = userService.findByCode(code);
        requestChecker.checkSetNewPasswordRequest(request, user);
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setCode("");
        userService.save(user);
        return new SimpleResponseDto(true);
    }

    public SimpleResponseDto logout(HttpServletRequest request) throws ServletException {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        sessionStorage.getSessions().remove(sessionId);
        request.logout();
        return new SimpleResponseDto(true);
    }

    //=====================================Additional method==============================

    private String createPasswordRestoreCode() {
        String code = RandomStringUtils.randomAlphanumeric(40);
        code += getEncodedCurrentTime();
        return code;
    }

    private String getEncodedCurrentTime() {
        long currentTimeMilli = Instant.now().toEpochMilli();
        String time = Long.toString(currentTimeMilli);
        return Base64.getEncoder().encodeToString(time.getBytes());
    }
}
