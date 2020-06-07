package blogengine.models.dto.blogdto.postdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class AddPostRequest {

    private String time;
    @JsonProperty("active")
    private boolean isActive;
    @Size(min = 10, message = "Заголовок не установлен")
    private String title;
    @Size(min = 100, message = "Текст публикации слишком короткий")
    private String text;
    @JsonProperty("tags")
    private Set<String> tagNames;
}
