package blogengine.exceptions;

import blogengine.models.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@ControllerAdvice
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({RootApplicationException.class})
    public final ResponseEntity<ErrorResponse> handleExceptionText(RootApplicationException ex){
        HashMap<String, String> errors = new HashMap<>();
        errors.put(ex.getSourceOfException(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }
}
