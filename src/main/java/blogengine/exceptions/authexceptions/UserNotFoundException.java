package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class UserNotFoundException extends AbstractBadRequestException {

    public UserNotFoundException(final String message) {
        super(message);
    }
}
