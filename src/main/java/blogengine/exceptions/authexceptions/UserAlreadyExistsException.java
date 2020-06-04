package blogengine.exceptions.authexceptions;

import blogengine.exceptions.RootApplicationException;

public class UserAlreadyExistsException extends RootApplicationException {

    public UserAlreadyExistsException(String message){
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "email";
    }
}
