package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractUnauthenticatedException;

public class UnauthenticatedUserException extends AbstractUnauthenticatedException {

    public UnauthenticatedUserException(String message) {
        super(message);
    }
}
