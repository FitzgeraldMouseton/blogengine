package blogengine.models.dto.blogdto.tagdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagsResponse {

    private SingleTagDto[] tags;
}
