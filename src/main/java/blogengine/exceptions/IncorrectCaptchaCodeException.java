package blogengine.exceptions;

public class IncorrectCaptchaCodeException extends Exception {

    public IncorrectCaptchaCodeException(String message){
        super(message);
    }
}
