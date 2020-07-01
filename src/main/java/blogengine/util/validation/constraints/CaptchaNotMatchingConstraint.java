package blogengine.util.validation.constraints;

import blogengine.util.validation.validators.CaptchaValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CaptchaValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CaptchaNotMatchingConstraint {

    String message() default "Код с картинки введён неверно";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
