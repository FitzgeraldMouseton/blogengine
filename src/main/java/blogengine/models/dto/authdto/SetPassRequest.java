package blogengine.models.dto.authdto;

import blogengine.util.validation.constraints.CaptchaNotMatchingConstraint;
import blogengine.util.validation.constraints.CaptchaNotExpiredConstraint;
import blogengine.util.validation.constraints.CodeNotExpiredConstraint;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SetPassRequest {

    private String password;

    @CodeNotExpiredConstraint
    private String code;

    @CaptchaNotMatchingConstraint
    @CaptchaNotExpiredConstraint
    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
