package blogengine.util.validation.validators;

import blogengine.models.CaptchaCode;
import blogengine.services.CaptchaService;
import blogengine.util.validation.constraints.CaptchaNotExpiredConstraint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@RequiredArgsConstructor
public class CaptchaExpirationValidator implements ConstraintValidator<CaptchaNotExpiredConstraint, String> {

    private final CaptchaService captchaService;
    @Value("${captcha.expiration.time}")
    private int captchaExpirationTime;

    @Override
    public void initialize(CaptchaNotExpiredConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(String captcha, ConstraintValidatorContext constraintValidatorContext) {
        CaptchaCode captchaCode = captchaService.findBySecretCode(captcha);
        if (captchaCode != null) {
            return captchaCode.getTime().plusSeconds(captchaExpirationTime).isAfter(LocalDateTime.now(ZoneOffset.UTC));
        }
        return false;
    }
}
