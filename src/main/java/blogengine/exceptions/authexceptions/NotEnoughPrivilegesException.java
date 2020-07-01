package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class NotEnoughPrivilegesException extends AbstractBadRequestException {

    public NotEnoughPrivilegesException(final String message) {
        super(message);
    }
}
