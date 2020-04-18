package blogengine.models.dto.postdto;

import blogengine.models.dto.CommentDTO;
import blogengine.models.dto.userdto.UserDTO;
import lombok.Data;

import java.util.List;

@Data
public class PostDTO {

    private int id;
    private String time;
    private UserDTO user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private String text;
    private List<CommentDTO> comments;
    private String[] tags;
}
