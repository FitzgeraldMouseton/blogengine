package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class UserAlreadyExistsException extends AbstractBadRequestException {

    public UserAlreadyExistsException(String message){
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "email";
    }
}
