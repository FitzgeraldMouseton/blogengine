package blogengine.models.dto.userdto;

import lombok.Data;

@Data
public class ChangeProfileRequest {

    private String photo;
    private int removePhoto;
    private String name;
    private String email;
    private String password;
}
