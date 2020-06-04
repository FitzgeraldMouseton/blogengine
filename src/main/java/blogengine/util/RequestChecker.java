package blogengine.util;

import blogengine.exceptions.authexceptions.*;
import blogengine.exceptions.blogexceptions.IncorrectTextException;
import blogengine.exceptions.blogexceptions.IncorrectTitleException;
import blogengine.models.CaptchaCode;
import blogengine.models.User;
import blogengine.models.dto.authdto.LoginRequest;
import blogengine.models.dto.authdto.RegisterRequest;
import blogengine.models.dto.authdto.SetPassRequest;
import blogengine.models.dto.blogdto.commentdto.CommentRequest;
import blogengine.services.CaptchaService;
import blogengine.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestChecker {

    private final UserService userService;
    private final CaptchaService captchaService;
    private final BCryptPasswordEncoder encoder;

    @Value("${restorecode.expiration.time}")
    private Integer restoreCodeExistenceTime;

    @Value("${textconstraints.minlength}")
    private Integer postMinLength;
    @Value("${textconstraints.mintitle}")
    private Integer postTitleMinLength;
    @Value("${textconstraints.mincomment}")
    private Integer commentMinLength;


    //==================================== AuthService methods requests ================================================

    public void checkLoginRequest(User user, LoginRequest request){
        if (user == null) {
            throw new UserNotFoundException("Не найден пользователь с таким адресом почты: " + request.getEmail());
        }
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new IncorrectCredentialsException("Неверно введены данные");
        }
    }

    public void checkRegistrationRequest(RegisterRequest registerRequest, CaptchaCode captchaFromDatabase) {
        if (userService.findByEmail(registerRequest.getEmail()) != null)
            throw new UserAlreadyExistsException("Этот e-mail уже зарегистрирован");
        if (captchaFromDatabase == null || !captchaFromDatabase.getCode().equals(registerRequest.getCaptchaCode())) {
            throw new IncorrectCaptchaCodeException("Код с картинки введён неверно");
        }
        deleteCaptchaIfExpired(captchaFromDatabase);
        String name = registerRequest.getName();
        if (name.length() < 3 || !name.chars().allMatch(Character::isAlphabetic)) {
            throw new IncorrectUsernameException("Имя указано неверно");
        }
    }

    public void checkSetNewPasswordRequest(SetPassRequest request, User user) {
        if (user == null) {
            throw new PasswordRestoreException("Ссылка для восстановления пароля устарела.<a href=\"/auth/restore\">Запросить ссылку снова</a>");
        }
        if (isRestoreCodeExpired(request.getCode())) {
            user.setCode("");
            userService.save(user);
            throw new PasswordRestoreException("Ссылка для восстановления пароля устарела.<a href=\"/auth/restore\">Запросить ссылку снова</a>");
        }
        CaptchaCode captchaCode = captchaService.findBySecretCode(request.getCaptchaSecret());
        if (captchaCode == null || !captchaCode.getCode().equals(request.getCode())) {
            throw new IncorrectCaptchaCodeException("Код с картинки введён неверно");
        }
        deleteCaptchaIfExpired(captchaCode);
    }

    private boolean isRestoreCodeExpired(String code) {
        String encodedTime = code.substring(40);
        String decodedTime = new String(Base64.getDecoder().decode(encodedTime));
        long time = Long.parseLong(decodedTime);
        Instant currentTime = Instant.now();
        Instant codeTime = Instant.ofEpochMilli(time);
        return currentTime.isAfter(codeTime.plusSeconds(restoreCodeExistenceTime));
    }

    private void deleteCaptchaIfExpired(CaptchaCode captchaCode){
        if (captchaCode.getTime().plusHours(1).isBefore(LocalDateTime.now())){
            captchaService.delete(captchaCode);
            throw new IncorrectCaptchaCodeException("Код с картинки введён неверно");
        }
    }

    //==================================== PostService methods requests ================================================

    public void checkPostParameters(String title, String text, LocalDateTime time){
        if (title == null || title.length() < postTitleMinLength) {
            throw new IncorrectTitleException("Заголовок не установлен");
        }
        if (text == null || text.length() < postMinLength) {
            throw new IncorrectTextException("Текст публикации слишком короткий");
        }
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if (time.isBefore(currentTime)){
            throw new IllegalArgumentException("Неправильная дата");
        }
    }

    public void checkCommentRequest(CommentRequest request){
        if (request.getText().isEmpty() || request.getText().length() < commentMinLength)
            throw new IncorrectTextException("Текст комментария не задан или слишком короткий");
    }
}
