package blogengine.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostsInfo {

    long count;
    List<PostDTO> posts;
}
