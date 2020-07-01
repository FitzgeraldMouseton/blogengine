package blogengine.models.dto.blogdto.commentdto;

import lombok.Data;

@Data
public class CommentResponse {
    private int id;

    public CommentResponse(final int id) {
        this.id = id;
    }
}
