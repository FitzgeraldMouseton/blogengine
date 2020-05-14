package blogengine.models.dto.blogdto;

import blogengine.models.Tag;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.mail.search.SearchTerm;
import java.util.Set;

@Data
public class AddPostRequest {

    private String time;
    @JsonProperty("active")
    private boolean isActive;
    private String title;
    private String text;
    private Set<Tag> tags;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
