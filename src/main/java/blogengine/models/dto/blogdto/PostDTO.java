package blogengine.models.dto.blogdto;

import blogengine.models.dto.userdto.UserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class PostDTO {

    public PostDTO(int id, String time, UserDTO user, String title, String announce, int likeCount, int dislikeCount, int commentCount, int viewCount, String text, List<CommentDTO> comments, String[] tags) {
        this.id = id;
        this.time = time;
        this.user = user;
        this.title = title;
        this.announce = announce;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.text = text;
        this.comments = comments;
        this.tags = tags;
    }

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
