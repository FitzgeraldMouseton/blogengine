package blogengine.models.dto.blogdto.postdto;

import blogengine.models.dto.blogdto.commentdto.CommentDTO;
import blogengine.models.dto.userdto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class PostDto {

    private int id;
    private String time;
    private UserDto user;
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
