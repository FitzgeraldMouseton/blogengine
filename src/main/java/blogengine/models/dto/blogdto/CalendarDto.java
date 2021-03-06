package blogengine.models.dto.blogdto;

import lombok.Data;
import java.util.Map;

@Data
public class CalendarDto {

    private Integer[] years;
    private Map<String, Long> posts;
}
