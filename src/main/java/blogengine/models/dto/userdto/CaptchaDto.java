package blogengine.models.dto.userdto;

import lombok.Data;

@Data
public class CaptchaDto {

    private String secret;
    private String image;
}
