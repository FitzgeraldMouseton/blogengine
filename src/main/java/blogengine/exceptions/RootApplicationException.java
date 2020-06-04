package blogengine.exceptions;

public abstract class RootApplicationException extends RuntimeException {

    public RootApplicationException(String message) {
        super(message);
    }

    public abstract String getSourceOfException();
}
