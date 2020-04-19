package blogengine.models.dto.blogdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingleTagDto {

    private String name;
    private double weight;
}
