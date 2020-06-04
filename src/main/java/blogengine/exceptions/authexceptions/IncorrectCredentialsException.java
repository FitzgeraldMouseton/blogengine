package blogengine.exceptions.authexceptions;

import blogengine.exceptions.RootApplicationException;

public class IncorrectCredentialsException extends RootApplicationException {

    public IncorrectCredentialsException(String message) {
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "message";
    }
}
