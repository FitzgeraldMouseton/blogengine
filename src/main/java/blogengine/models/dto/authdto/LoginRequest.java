package blogengine.models.dto.authdto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginRequest {

    private String e_mail;
    private String password;

    public String getEmail(){
        return e_mail;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String e_mail) {
        this.e_mail = e_mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
