package blogengine.models.dto.blogdto.postdto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostsInfo<T> {

    long count;
    List<T> posts;
}
