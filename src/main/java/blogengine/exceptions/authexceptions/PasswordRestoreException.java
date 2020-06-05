package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class PasswordRestoreException extends AbstractBadRequestException {

    public PasswordRestoreException(String message) {
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "code";
    }
}
