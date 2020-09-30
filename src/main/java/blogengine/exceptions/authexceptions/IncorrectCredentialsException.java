package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractAuthException;

public class IncorrectCredentialsException extends AbstractAuthException {

    public IncorrectCredentialsException(final String message) {
        super(message);
    }
}
