package blogengine.models.dto.blogdto.commentdto;

import blogengine.models.postconstants.PostConstraints;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class CommentRequest {

    @JsonProperty("parent_id")
    private String parentId;

    @JsonProperty("post_id")
    private String postId;

    @Size(min = PostConstraints.MIN_COMMENT_SIZE, message = "Текст комментария не задан или слишком короткий")
    private String text;
}
