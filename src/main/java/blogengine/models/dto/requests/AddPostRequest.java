package blogengine.models.dto.requests;

import blogengine.models.Tag;
import lombok.Data;

import java.util.Date;

@Data
public class AddPostRequest {

    private Date date;
    private Boolean active;
    private String title;
    private String text;
    private Tag[] tags;
}
