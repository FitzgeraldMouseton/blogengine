package blogengine.util.validation.validators;

import blogengine.services.UserService;
import blogengine.util.validation.constraints.EmailUniqueConstraint;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class EmailUniquenessValidator implements ConstraintValidator<EmailUniqueConstraint, String> {

    private final UserService userService;

    @Override
    public void initialize(EmailUniqueConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return userService.findByEmail(email) == null;
    }
}
