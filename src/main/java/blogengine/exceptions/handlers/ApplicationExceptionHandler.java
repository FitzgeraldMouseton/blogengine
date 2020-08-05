package blogengine.exceptions.handlers;

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
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({AbstractBadRequestException.class})
    public final ResponseEntity<ErrorResponse> handleApplicationExceptions(final AbstractBadRequestException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.prefix(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }

    @ExceptionHandler(AbstractUnauthenticatedException.class)
    protected final ResponseEntity<ErrorResponse> handleUnauthenticatedException(final AbstractUnauthenticatedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.prefix(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected final ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        AtomicBoolean isUnauthorized = new AtomicBoolean(false);
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    errors.put(error.getField(), error.getDefaultMessage());
                    if ("captcha".equals(error.getField())) {
                        isUnauthorized.set(true);
                    }
                    log.info(error.getField());
                } );

        errors.forEach((k, v) -> log.info(k + ": " + v));
        if (isUnauthorized.get()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }

    @ExceptionHandler(FileUploadBase.FileSizeLimitExceededException.class)
    public final ResponseEntity<ErrorResponse> handleFileSizeLimitExceededException(final FileUploadBase.FileSizeLimitExceededException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getFieldName(), ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }
}
