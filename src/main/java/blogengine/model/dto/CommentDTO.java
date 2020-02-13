package blogengine.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentDTO {

    private int id;
    private Date time;
    private UserDTO user;
    private String text;
}
