package blogengine.models.dto.authdto;

import blogengine.util.validation.constraints.CaptchaNotMatchingConstraint;
import blogengine.util.validation.constraints.CaptchaNotExpiredConstraint;
import blogengine.util.validation.constraints.EmailUniqueConstraint;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class RegisterRequest {

    @JsonProperty("e_mail")
    @EmailUniqueConstraint
    private String email;

    @Size(min = 2, message = "Имя указано неверно")
    private String name;

    private String password;

    @CaptchaNotMatchingConstraint
    @CaptchaNotExpiredConstraint
    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
