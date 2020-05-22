package blogengine.models.dto.blogdto.commentdto;

import lombok.Data;

@Data
public class CommentResponse {
    private int id;

    public CommentResponse(int id) {
        this.id = id;
    }
}
