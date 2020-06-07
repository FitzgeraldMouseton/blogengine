package blogengine.util.validation.validators;

import blogengine.models.CaptchaCode;
import blogengine.services.CaptchaService;
import blogengine.util.validation.constraints.CaptchaNotExpiredConstraint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class CaptchaExpirationValidator implements ConstraintValidator<CaptchaNotExpiredConstraint, String> {

    private final CaptchaService captchaService;

    @Override
    public void initialize(CaptchaNotExpiredConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(String captcha, ConstraintValidatorContext constraintValidatorContext) {

        CaptchaCode captchaCode = captchaService.findByCode(captcha);
        if (captchaCode != null){
            return captchaCode.getTime().plusSeconds(3600).isAfter(LocalDateTime.now());
        }
        return false;
    }
}
