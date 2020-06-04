package blogengine.exceptions.authexceptions;

import blogengine.exceptions.RootApplicationException;

public class IncorrectCaptchaCodeException extends RootApplicationException {

    public IncorrectCaptchaCodeException(String message){
        super(message);
    }

    public String getSourceOfException() {
        return "captcha";
    }
}
