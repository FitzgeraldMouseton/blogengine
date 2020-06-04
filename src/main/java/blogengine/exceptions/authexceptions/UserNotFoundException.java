package blogengine.exceptions.authexceptions;

import blogengine.exceptions.RootApplicationException;

public class UserNotFoundException extends RootApplicationException {

    public UserNotFoundException(String message){
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "message";
    }
}
