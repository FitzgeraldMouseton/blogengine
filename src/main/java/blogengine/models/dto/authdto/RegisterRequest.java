package blogengine.models.dto.authdto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RegisterRequest {

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private String e_mail;
    private String name;
    private String password;
    private String captcha;
    private String captcha_secret;

    public String getEmail() {
        return e_mail;
    }
    public void setEmail(String e_mail) {
        this.e_mail = e_mail;
    }
}
