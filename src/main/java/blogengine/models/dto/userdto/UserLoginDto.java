package blogengine.models.dto.userdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserLoginDto {

    private int id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
}