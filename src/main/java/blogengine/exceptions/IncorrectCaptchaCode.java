package blogengine.exceptions;

public class IncorrectCaptchaCode extends Exception {

    public IncorrectCaptchaCode(String message){
        super(message);
    }
}
