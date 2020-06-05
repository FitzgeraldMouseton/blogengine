package blogengine.exceptions;

import blogengine.models.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * Класс содержит методы для отлова и обработки кастомных исключений,
 * возникающих во время работы приложения
 */

@ControllerAdvice
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({AbstractBadRequestException.class})
    public final ResponseEntity<ErrorResponse> handleApplicationExceptions(AbstractBadRequestException ex){
        HashMap<String, String> errors = new HashMap<>();
        errors.put(ex.getSourceOfException(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }
}
