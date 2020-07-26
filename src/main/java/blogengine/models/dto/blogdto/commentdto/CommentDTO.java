package blogengine.models.dto.blogdto.commentdto;

import blogengine.models.dto.userdto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDTO {

    public CommentDTO(String text) {
        this.text = text;
    }

    private int id;
    private String time;
    private String text;
    private UserDto user;
}
