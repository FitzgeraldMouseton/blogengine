package blogengine.models.dto.authdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {

    @JsonProperty("e_mail")
    private String email;
    private String password;
}
