package blogengine.exceptions.authexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class UserNotFoundException extends AbstractBadRequestException {

    public UserNotFoundException(String message){
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "message";
    }
}
