package blogengine.util.validation.validators;

import blogengine.services.CaptchaService;
import blogengine.util.validation.constraints.CaptchaNotMatchingConstraint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
@RequiredArgsConstructor
public class CaptchaValidator implements ConstraintValidator<CaptchaNotMatchingConstraint, String> {

    private final CaptchaService captchaService;

    @Override
    public void initialize(CaptchaNotMatchingConstraint captchaConstraint) {
    }

    @Override
    public boolean isValid(final String captcha, final ConstraintValidatorContext cvx) {
        if (captcha == null || captchaService.findByCode(captcha) == null) {
//            cvx.disableDefaultConstraintViolation();
//            cvx.buildConstraintViolationWithTemplate("{cvx: Код с картинки введён неверно}").addPropertyNode("captcha").addConstraintViolation();
            return false;
        }
        return true;
    }
}
