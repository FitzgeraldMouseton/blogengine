package blogengine.util.validation.validators;

import blogengine.services.UserService;
import blogengine.util.validation.constraints.EmailUniqueConstraint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
@RequiredArgsConstructor
public class EmailUniquenessValidator implements ConstraintValidator<EmailUniqueConstraint, String> {

    private final UserService userService;

    @Override
    public void initialize(EmailUniqueConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(final String email, final ConstraintValidatorContext constraintValidatorContext) {
        return userService.findByEmail(email) == null;
    }
}
