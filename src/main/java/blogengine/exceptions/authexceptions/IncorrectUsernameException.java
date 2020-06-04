package blogengine.exceptions.authexceptions;

import blogengine.exceptions.RootApplicationException;

public class IncorrectUsernameException extends RootApplicationException {

    public IncorrectUsernameException(String message) {
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "name";
    }
}
