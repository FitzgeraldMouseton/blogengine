package blogengine.exceptions;

public abstract class AbstractBadRequestException extends RuntimeException {

    public AbstractBadRequestException(String message) {
        super(message);
    }

    public abstract String getSourceOfException();
}
