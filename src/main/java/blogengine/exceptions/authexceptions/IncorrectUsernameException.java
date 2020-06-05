package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class IncorrectUsernameException extends AbstractBadRequestException {

    public IncorrectUsernameException(String message) {
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "name";
    }
}
