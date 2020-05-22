package blogengine.models.dto.blogdto.commentdto;

import blogengine.models.dto.userdto.UserDto;
import lombok.Data;

@Data
public class CommentDTO {

    private int id;
    private String time;
    private String text;
    private UserDto user;
}
