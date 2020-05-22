package blogengine.models.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class ErrorResponse {

    private boolean result = false;
    private HashMap<String, String> errors;

    public ErrorResponse(HashMap<String, String> errors) {
        this.errors = errors;
    }
}
