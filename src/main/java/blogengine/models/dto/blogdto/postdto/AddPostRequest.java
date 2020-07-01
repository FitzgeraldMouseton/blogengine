package blogengine.models.dto.blogdto.postdto;

import blogengine.models.Tag;
import blogengine.models.postconstants.PostConstraints;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class AddPostRequest {

    private String time;

    @JsonProperty("active")
    private boolean isActive;

    @Size(min = PostConstraints.MIN_TITLE_SIZE, message = "Заголовок не установлен")
    private String title;

    @Size(min = PostConstraints.MIN_TEXT_SIZE, message = "Текст публикации слишком короткий")
    private String text;

    @JsonProperty("tags")
    private Tag[] tagNames;
}
