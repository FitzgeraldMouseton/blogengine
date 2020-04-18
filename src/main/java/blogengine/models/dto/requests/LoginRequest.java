package blogengine.models.dto.requests;

public class LoginRequest {

    private String e_mail;
    private String password;

    public String getEmail(){
        return e_mail;
    }

    public String getPassword() {
        return password;
    }
}
