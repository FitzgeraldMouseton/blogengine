package blogengine.services;

import blogengine.exceptions.authexceptions.IncorrectCredentialsException;
import blogengine.exceptions.authexceptions.InvalidCaptchaCodeException;
import blogengine.exceptions.blogexeptions.PageNotFoundException;
import blogengine.mappers.UserDtoMapper;
import blogengine.models.CaptchaCode;
import blogengine.models.User;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.authdto.*;
import blogengine.util.DBEventsCreator;
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
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final SettingService settingService;
    private final EmailServiceImpl emailService;
    private final CaptchaService captchaService;
    private final SessionStorage sessionStorage;
    private final UserDtoMapper userDtoMapper;
    private final BCryptPasswordEncoder encoder;
    private final DBEventsCreator dbEventsCreator;

    @Value("${restore_code.code_length}")
    private int restoreCodeLength;
    @Value("${restore_code.expiration_time}")
    private int restoreCodeExpirationTime;
    @Value("${restore_code.path}")
    private String restoreCodePath;

    @Transactional
    public AuthenticationResponse login(final LoginRequest loginRequest) {
        User user = userService.findByEmail(loginRequest.getEmail());
        if (user == null || !encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IncorrectCredentialsException("Логин и/или пароль введен(ы) неверно");
        }
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Integer userId = user.getId();
        sessionStorage.getSessions().put(sessionId, userId);
        UserLoginResponse loginDto = userDtoMapper.userToLoginResponse(user);
        return new AuthenticationResponse(true, loginDto);
    }

    @Transactional
    public SimpleResponseDto register(final RegisterRequest registerRequest) {
        if (!settingService.isMultiUserEnabled()) {
            throw new PageNotFoundException();
        }
        CaptchaCode captchaCode = captchaService.findBySecretCode(registerRequest.getCaptchaSecret());
        if (!captchaCode.getCode().equals(registerRequest.getCaptcha())) {
            throw new InvalidCaptchaCodeException("Код с картинки введён неверно");
        }
        User user = userDtoMapper.registerRequestToUser(registerRequest);
        user.setPassword(encoder.encode(user.getPassword()));
        userService.save(user);
        return new SimpleResponseDto(true);
    }

    public AuthenticationResponse check() {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (sessionStorage.getSessions().containsKey(sessionId)) {
            int userId = sessionStorage.getSessions().get(sessionId);
            User user = userService.findById(userId);
            UserLoginResponse userDTO = userDtoMapper.userToLoginResponse(user);
            return new AuthenticationResponse(true, userDTO);
        }
        return null;
    }

    @Transactional
    public SimpleResponseDto sendRestorePasswordMessage(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return new SimpleResponseDto(false);
        }
        String code = createPasswordRestoreCode();
        user.setCode(code);
        userService.save(user);
        dbEventsCreator.deleteRestoreCodeWhenExpired(code, restoreCodeExpirationTime);
        String message = "Для восстановления пароля перейдите по ссылке " + restoreCodePath + code;
        emailService.send(email, "Восстановление пароля", message);
        return new SimpleResponseDto(true);
    }

    public SimpleResponseDto setNewPassword(final SetPassRequest request) {
        CaptchaCode captchaCode = captchaService.findBySecretCode(request.getCaptchaSecret());
        if (!captchaCode.getCode().equals(request.getCaptcha())) {
            throw new InvalidCaptchaCodeException("Код с картинки введён неверно");
        }
        String code = request.getCode();
        User user = userService.findByCode(code);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setCode(null);
        userService.save(user);
        return new SimpleResponseDto(true);
    }

    public SimpleResponseDto logout(final HttpServletRequest request) throws ServletException {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        sessionStorage.getSessions().remove(sessionId);
        request.logout();
        return new SimpleResponseDto(true);
    }

    //=====================================Additional method==============================

    private String createPasswordRestoreCode() {
        String code = RandomStringUtils.randomAlphanumeric(restoreCodeLength);
        code += getEncodedCurrentTime();
        return code;
    }

    private String getEncodedCurrentTime() {
        long currentTimeMilli = Instant.now(Clock.systemUTC()).toEpochMilli();
        String time = Long.toString(currentTimeMilli);
        return Base64.getEncoder().encodeToString(time.getBytes());
    }

//    public boolean isRestoreCodeExpired(final String code) {
//        if (code == null || code.isEmpty()) {
//            return true;
//        }
//        String encodedTime = code.substring(restoreCodeLength);
//        String decodedTime = new String(Base64.getDecoder().decode(encodedTime));
//        long time = Long.parseLong(decodedTime);
//        Instant currentTime = Instant.now();
//        Instant codeTime = Instant.ofEpochMilli(time);
//        return currentTime.isAfter(codeTime.plusSeconds(restoreCodeExpirationTime));
//    }
}
