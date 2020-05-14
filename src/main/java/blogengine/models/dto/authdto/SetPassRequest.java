package blogengine.models.dto.authdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SetPassRequest {

    private String password;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
