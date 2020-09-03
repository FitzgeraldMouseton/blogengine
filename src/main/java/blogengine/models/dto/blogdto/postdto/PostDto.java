package blogengine.models.dto.blogdto.postdto;

import blogengine.models.dto.blogdto.commentdto.CommentDTO;
import blogengine.models.dto.userdto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private int id;
    private long timestamp;
    private boolean active;
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
