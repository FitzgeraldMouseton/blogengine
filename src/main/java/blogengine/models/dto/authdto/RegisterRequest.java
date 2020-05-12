package blogengine.models.dto.authdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequest {

    @JsonProperty("e_mail")
    private String email;
    private String name;
    private String password;
    private String captchaCode;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
