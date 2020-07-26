package blogengine.models.dto.authdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class LoginRequest {

    @Email
    @JsonProperty("e_mail")
    private String email;
    private String password;
}
