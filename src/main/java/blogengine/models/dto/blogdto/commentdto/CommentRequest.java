package blogengine.models.dto.blogdto.commentdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class CommentRequest {

    @JsonProperty("parent_id")
    private String parentId;
    @JsonProperty("post_id")
    private String postId;
    @Size(min = 6, message = "Текст комментария не задан или слишком короткий")
    private String text;
}
