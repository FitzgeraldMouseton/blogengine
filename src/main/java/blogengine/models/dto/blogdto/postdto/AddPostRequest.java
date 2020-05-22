package blogengine.models.dto.blogdto.postdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
public class AddPostRequest {

    private String time;
    @JsonProperty("active")
    private boolean isActive;
    private String title;
    private String text;
    @JsonProperty("tags")
    private Set<String> tagNames;
}
