package blogengine.models.dto.authdto;

import lombok.Data;

@Data
public class SetPassRequest {

    private String password;
    private String captcha;
    private String captcha_secret;
}
