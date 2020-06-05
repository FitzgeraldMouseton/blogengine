package blogengine.exceptions.blogexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class IncorrectTitleException extends AbstractBadRequestException {

    public IncorrectTitleException(String message) {
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "title";
    }
}
