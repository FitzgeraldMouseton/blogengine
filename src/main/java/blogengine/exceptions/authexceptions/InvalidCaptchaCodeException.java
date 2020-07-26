package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class InvalidCaptchaCodeException extends AbstractBadRequestException {

    public InvalidCaptchaCodeException(String message) {
        super(message);
    }
}
