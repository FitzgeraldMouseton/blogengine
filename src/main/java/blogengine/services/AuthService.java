package blogengine.services;

import blogengine.exceptions.IncorrectCaptchaCodeException;
import blogengine.exceptions.UserAlreadyExistsException;
import blogengine.mappers.UserDtoMapper;
import blogengine.models.CaptchaCode;
import blogengine.models.User;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.authdto.*;
import blogengine.util.SessionStorage;
import blogengine.util.mail.EmailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;

import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@Slf4j
@Service
public class AuthService {

    private UserService userService;
    private UserDtoMapper userDtoMapper;
    private EmailServiceImpl emailService;
    private CaptchaService captchaService;
    private SessionStorage sessionStorage;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ThreadPoolTaskScheduler taskScheduler;

    public AuthService(UserService userService, UserDtoMapper userDtoMapper, EmailServiceImpl emailService, CaptchaService captchaService, SessionStorage sessionStorage, BCryptPasswordEncoder bCryptPasswordEncoder, ThreadPoolTaskScheduler taskScheduler) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
        this.emailService = emailService;
        this.captchaService = captchaService;
        this.sessionStorage = sessionStorage;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.taskScheduler = taskScheduler;
    }

    @Value("${captcha.expire.delay}")
    private Integer captchaExistenceTime;
    @Value("${restorecode.expire.delay}")
    private Integer restoreCodeExistenceTime;

    @Transactional
    public AuthenticationResponse login(LoginRequest loginRequest) {
        AuthenticationResponse loginResponse = null;
        User user = userService.findByEmail(loginRequest.getEmail());
        if (user == null){
            throw new NoResultException("Не найден пользователь с таким адресом почты: " + loginRequest.getEmail());
        }
        if (bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
            Integer userId = user.getId();
            sessionStorage.getSessions().put(sessionId, userId);
            UserLoginDto loginDto = userDtoMapper.userToLoginDto(user);
            loginResponse = new AuthenticationResponse(true, loginDto);
        }
        return loginResponse;
    }

    @Transactional
    public SimpleResponseDto register(RegisterRequest registerRequest) throws Exception {

        if (userService.findByEmail(registerRequest.getEmail()) != null)
            throw new UserAlreadyExistsException("Этот e-mail уже зарегистрирован");
        CaptchaCode captcha = captchaService.findBySecretCode(registerRequest.getCaptchaSecret());
        if (captcha == null || !captcha.getCode().equals(registerRequest.getCaptchaCode())){
            throw new IncorrectCaptchaCodeException("Код с картинки введён неверно");
        }
//        String name = registerRequest.getName();
//        if (name.length() < 3 || !name.chars().allMatch(Character::isAlphabetic)){
//            throw new IllegalArgumentException("Имя указано неверно");
//        }
        User user = userDtoMapper.registerDtoToUser(registerRequest);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userService.save(user);
        taskScheduler.schedule(() -> captchaService.deleteCaptchaCodeBySecretCode(captcha.getSecretCode()),
                                    Instant.now().plusSeconds(captchaExistenceTime));
        return new SimpleResponseDto(true);
    }

    public AuthenticationResponse check(){
        AuthenticationResponse authenticationResponse = null;
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (!sessionStorage.getSessions().isEmpty()){
            int userId = sessionStorage.getSessions().get(sessionId);
            User user = userService.findById(userId);
            UserLoginDto userDTO = userDtoMapper.userToLoginDto(user);
            authenticationResponse = new AuthenticationResponse(true, userDTO);
        }
        return authenticationResponse;
    }

    @Transactional
    public SimpleResponseDto sendRestorePasswordMessage(String email){
        String code = RandomStringUtils.randomAlphanumeric(40);
        User user = userService.findByEmail(email);
        if (user == null)
            return new SimpleResponseDto(false);
        user.setCode(code);
        userService.save(user);
        String message = "Для восстановления пароля перейдите по ссылке http://localhost:8080/login/change-password/" + code;
        emailService.send(email, "Васстановление пароля", message);
        taskScheduler.schedule(() -> {
            user.setCode("");
            userService.save(user);
        }, Instant.now().plusSeconds(restoreCodeExistenceTime));
        return new SimpleResponseDto(true);
    }

    public boolean setNewPassword (String code, SetPassRequest dto) throws IllegalStateException, IllegalArgumentException {

        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (sessionStorage.getSessions().isEmpty()){
            throw new UsernameNotFoundException("");
        }
        int userId = sessionStorage.getSessions().get(sessionId);
        User user = userService.findById(userId);
        if (!user.getCode().equals(code)){
            throw new IllegalStateException("Ссылка для восстановления пароля устарела.<a href=\"/auth/restore\">Запросить ссылку снова</a>");
        }
        CaptchaCode captchaCode = captchaService.findByCode(dto.getCaptcha());
        if(captchaCode == null){
            throw new IllegalArgumentException("Код с картинки введён неверно");
        }
        if (captchaCode.getSecretCode().equals(dto.getCaptcha_secret())) {
            user.setPassword(dto.getPassword());
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
}
