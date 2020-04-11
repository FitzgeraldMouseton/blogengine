package blogengine.models.dto.postdto;

import blogengine.models.dto.postdto.PostDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostsInfo {

    long count;
    List<PostDTO> posts;
}
