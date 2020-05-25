package blogengine.models.dto.userdto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChangeProfileRequest {

    private MultipartFile photo;
    private int removePhoto;
    private String name;
    private String email;
    private String password;
}
