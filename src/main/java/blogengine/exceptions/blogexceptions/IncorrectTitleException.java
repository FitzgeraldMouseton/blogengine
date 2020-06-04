package blogengine.exceptions.blogexceptions;

import blogengine.exceptions.RootApplicationException;

public class IncorrectTitleException extends RootApplicationException {

    public IncorrectTitleException(String message) {
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "title";
    }
}
