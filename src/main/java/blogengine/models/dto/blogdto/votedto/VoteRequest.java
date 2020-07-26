package blogengine.models.dto.blogdto.votedto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoteRequest {

    @JsonProperty("post_id")
    private int postId;

    public VoteRequest(int postId) {
        this.postId = postId;
    }
}
