package blogengine.models.dto.authdto;

import blogengine.util.validation.constraints.CaptchaNotExpiredConstraint;
import blogengine.util.validation.constraints.CodeNotExpiredConstraint;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetPassRequest {

    private String password;

    @CodeNotExpiredConstraint
    private String code;

    private String captcha;

    @CaptchaNotExpiredConstraint
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
