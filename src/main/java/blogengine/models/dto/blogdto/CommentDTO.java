package blogengine.models.dto.blogdto;

import blogengine.models.dto.userdto.UserDTO;
import lombok.Data;

import java.util.Date;

@Data
public class CommentDTO {

    private int id;
    private String time;
    private String text;
    private UserDTO user;
}
