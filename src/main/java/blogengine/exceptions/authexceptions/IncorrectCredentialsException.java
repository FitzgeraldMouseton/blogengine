package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class IncorrectCredentialsException extends AbstractBadRequestException {

    public IncorrectCredentialsException(String message) {
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "message";
    }
}
