package blogengine.exceptions.exeptionhandlers;

import blogengine.exceptions.AbstractAuthException;
import blogengine.exceptions.AbstractBadRequestException;
import blogengine.exceptions.AbstractUnauthenticatedException;
import blogengine.models.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({AbstractBadRequestException.class})
    public final ResponseEntity<Object> handleApplicationExceptions(final AbstractBadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(AbstractUnauthenticatedException.class)
    protected final ResponseEntity<Object> handleUnauthenticatedException(final AbstractUnauthenticatedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(AbstractAuthException.class)
    protected final ResponseEntity<ErrorResponse> handleAuthException(final AbstractAuthException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.prefix(), ex.getMessage());
        return ResponseEntity.ok().body(new ErrorResponse(errors));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected final ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.ok().body(new ErrorResponse(errors));
    }

    @ExceptionHandler(FileUploadBase.FileSizeLimitExceededException.class)
    public final ResponseEntity<ErrorResponse> handleFileSizeLimitExceededException(final FileUploadBase.FileSizeLimitExceededException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getFieldName(), ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }

//    @ExceptionHandler({RuntimeException.class})
//    public final void handleRuntimeExceptions(final RuntimeException ex) {
//        log.info("rtytyui");
//    }
}
