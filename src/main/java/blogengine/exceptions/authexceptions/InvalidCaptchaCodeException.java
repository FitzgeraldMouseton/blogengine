package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractAuthException;

public class InvalidCaptchaCodeException extends AbstractAuthException {

    public InvalidCaptchaCodeException(String message) {
        super(message);
    }

    public String prefix() {
        return "captcha";
    }
}
