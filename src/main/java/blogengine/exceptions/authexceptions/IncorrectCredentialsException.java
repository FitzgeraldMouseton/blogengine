package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractAuthException;
import blogengine.exceptions.AbstractBadRequestException;

public class IncorrectCredentialsException extends AbstractAuthException {

    public IncorrectCredentialsException(final String message) {
        super(message);
    }
}
