package blogengine.models.dto.authdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {

    private boolean result;
    private UserLoginDto user;
}
