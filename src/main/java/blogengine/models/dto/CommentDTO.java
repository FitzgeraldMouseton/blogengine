package blogengine.models.dto;

import blogengine.models.dto.userdto.UserDTO;
import lombok.Data;

import java.util.Date;

@Data
public class CommentDTO {

    private int id;
    private Date time;
    private UserDTO user;
    private String text;
}
