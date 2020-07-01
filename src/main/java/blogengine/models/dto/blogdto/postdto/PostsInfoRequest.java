package blogengine.models.dto.blogdto.postdto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostsInfoRequest<T> {

    private long count;
    private List<T> posts;
}
