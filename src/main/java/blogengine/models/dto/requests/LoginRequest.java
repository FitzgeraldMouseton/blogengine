package blogengine.models.dto.requests;

import lombok.Data;

@Data
public class LoginRequest {

    private String e_mail;
    private String password;

    public String getEmail(){
        return e_mail;
    }
}
