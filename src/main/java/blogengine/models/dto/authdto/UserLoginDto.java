package blogengine.models.dto.authdto;

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
