package blogengine.exceptions.blogexceptions;

import blogengine.exceptions.AbstractBadRequestException;

public class IncorrectTextException extends AbstractBadRequestException {

    public IncorrectTextException(String message) {
        super(message);
    }

    @Override
    public String getSourceOfException() {
        return "text";
    }
}
