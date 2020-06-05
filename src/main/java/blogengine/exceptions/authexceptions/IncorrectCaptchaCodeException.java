package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class IncorrectCaptchaCodeException extends AbstractBadRequestException {

    public IncorrectCaptchaCodeException(String message){
        super(message);
    }

    public String getSourceOfException() {
        return "captcha";
    }
}
