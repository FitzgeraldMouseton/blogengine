package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class IncorrectCredentialsException extends AbstractBadRequestException {

    public IncorrectCredentialsException(final String message) {
        super(message);
    }

}
