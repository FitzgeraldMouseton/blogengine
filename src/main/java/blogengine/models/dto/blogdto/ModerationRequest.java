package blogengine.models.dto.blogdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ModerationRequest {

    @JsonProperty("post_id")
    private Integer postId;
    private String decision;
}
