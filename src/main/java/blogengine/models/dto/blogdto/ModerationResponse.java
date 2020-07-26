package blogengine.models.dto.blogdto;

import blogengine.models.dto.userdto.UserDto;
import lombok.Data;

@Data
public class ModerationResponse {

    private int id;
    private long timestamp;
    private UserDto user;
    private String title;
    private String announce;
}
