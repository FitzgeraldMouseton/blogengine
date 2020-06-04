package blogengine.exceptions.blogexceptions;

import blogengine.exceptions.RootApplicationException;

public class IncorrectTextException extends RootApplicationException {

    public IncorrectTextException(String message) {
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "text";
    }
}
