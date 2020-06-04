package blogengine.services;

import blogengine.exceptions.authexceptions.*;
import blogengine.mappers.UserDtoMapper;
import blogengine.models.CaptchaCode;
import blogengine.models.User;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.authdto.*;
import blogengine.util.SessionStorage;
import blogengine.util.mail.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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
    private final ThreadPoolTaskScheduler taskScheduler;

    @Value("${captcha.expiration.time}")
    private Integer captchaExistenceTime;
    @Value("${restorecode.expiration.time}")
    private Integer restoreCodeExistenceTime;

    @Transactional
    public AuthenticationResponse login(LoginRequest loginRequest) throws UserNotFoundException {
        log.info(captchaExistenceTime.toString());
        log.info(restoreCodeExistenceTime.toString());
        AuthenticationResponse loginResponse;
        User user = userService.findByEmail(loginRequest.getEmail());
        if (user == null){
            throw new UserNotFoundException("Не найден пользователь с таким адресом почты: " + loginRequest.getEmail());
        }
        if (bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
            Integer userId = user.getId();
            sessionStorage.getSessions().put(sessionId, userId);
            UserLoginDto loginDto = userDtoMapper.userToLoginDto(user);
            loginResponse = new AuthenticationResponse(true, loginDto);
        } else {
            throw new IncorrectCredentialsException("Неверно введены данные");
        }
        return loginResponse;
    }

    @Transactional
    public SimpleResponseDto register(RegisterRequest registerRequest) throws UserAlreadyExistsException, IncorrectCaptchaCodeException {

        if (userService.findByEmail(registerRequest.getEmail()) != null)
            throw new UserAlreadyExistsException("Этот e-mail уже зарегистрирован");
        CaptchaCode captcha = captchaService.findBySecretCode(registerRequest.getCaptchaSecret());
        if (captcha == null || !captcha.getCode().equals(registerRequest.getCaptchaCode())){
            throw new IncorrectCaptchaCodeException("Код с картинки введён неверно");
        }
        String name = registerRequest.getName();
        if (name.length() < 3 || !name.chars().allMatch(Character::isAlphabetic)){
            throw new IncorrectUsernameException("Имя указано неверно");
        }
        User user = userDtoMapper.registerDtoToUser(registerRequest);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userService.save(user);
        taskScheduler.schedule(() -> captchaService.deleteCaptchaCodeBySecretCode(captcha.getSecretCode()),
                                    Instant.now().plusSeconds(captchaExistenceTime));
        return new SimpleResponseDto(true);
    }

    public AuthenticationResponse check() {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (!sessionStorage.getSessions().values().isEmpty()){
            int userId = sessionStorage.getSessions().get(sessionId);
            User user = userService.findById(userId);
            UserLoginDto userDTO = userDtoMapper.userToLoginDto(user);
            return new AuthenticationResponse(true, userDTO);
        }
        return null;
    }

    @Transactional
    public SimpleResponseDto sendRestorePasswordMessage(String email){
        User user = userService.findByEmail(email);
        if (user == null)
            return new SimpleResponseDto(false);
        String code = createPasswordRestoreCode();
        user.setCode(code);
        userService.save(user);
        String message = "Для восстановления пароля перейдите по ссылке http://localhost:8080/login/change-password/" + code;
        emailService.send(email, "Васстановление пароля", message);
//        taskScheduler.schedule(() -> {
//            user.setCode("");
//            userService.save(user);
//        }, Instant.now().plusSeconds(restoreCodeExistenceTime));
        return new SimpleResponseDto(true);
    }

    public boolean setNewPassword (SetPassRequest request) {
        String code = request.getCode();
        User user = userService.findByCode(code);
        if (user == null){
            throw new PasswordRestoreException("Ссылка для восстановления пароля устарела.<a href=\"/auth/restore\">Запросить ссылку снова</a>");
        }
        if (isRestoreCodeExpired(code)){
            user.setCode("");
            userService.save(user);
            throw new PasswordRestoreException("Ссылка для восстановления пароля устарела.<a href=\"/auth/restore\">Запросить ссылку снова</a>");
        }
        CaptchaCode captchaCode = captchaService.findByCode(request.getCaptcha());
        if(captchaCode == null){
            throw new IncorrectCaptchaCodeException("Код с картинки введён неверно");
        }
        if (captchaCode.getSecretCode().equals(request.getCaptchaSecret())) {
            user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
            user.setCode("");
            userService.save(user);
            return true;
        }
        return false;
    }

    public SimpleResponseDto logout(HttpServletRequest request) throws ServletException {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        sessionStorage.getSessions().remove(sessionId);
        request.logout();
        return new SimpleResponseDto(true);
    }

    private String createPasswordRestoreCode(){
        String code = RandomStringUtils.randomAlphanumeric(40);
        code += getEncodedCurrentTime();
        return code;
    }

    private boolean isRestoreCodeExpired(String code){
        String encodedTime = code.substring(40);
        String decodedTime = new String(Base64.getDecoder().decode(encodedTime));
        long time = Long.parseLong(decodedTime);
        Instant currentTime = Instant.now();
        Instant codeTime = Instant.ofEpochMilli(time);
        return currentTime.isAfter(codeTime.plusSeconds(restoreCodeExistenceTime));
    }

    private String getEncodedCurrentTime(){
        long currentTimeMilli = Instant.now().toEpochMilli();
        String time = Long.toString(currentTimeMilli);
        return Base64.getEncoder().encodeToString(time.getBytes());
    }
}
