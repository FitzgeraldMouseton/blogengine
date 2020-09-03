package blogengine.exceptions.blogexeptions;

import blogengine.exceptions.AbstractBadRequestException;

public class NoSuchPostException extends AbstractBadRequestException {

    public NoSuchPostException(String message) {
        super(message);
    }
}
