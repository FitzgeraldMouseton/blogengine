package blogengine.exceptions.authexceptions;

import blogengine.exceptions.RootApplicationException;

public class PasswordRestoreException extends RootApplicationException {

    public PasswordRestoreException(String message) {
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "code";
    }
}
