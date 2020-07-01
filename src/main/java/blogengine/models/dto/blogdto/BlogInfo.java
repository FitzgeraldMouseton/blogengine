package blogengine.models.dto.blogdto;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Component
public class BlogInfo implements Serializable {

    @Value("${blog_info.title}")
    private String title;
    @Value("${blog_info.subtitle}")
    private String subtitle;
    @Value("${blog_info.phone}")
    private String phone;
    @Value("${blog_info.email}")
    private String email;
    @Value("${blog_info.copyright}")
    private String copyright;
    @Value("${blog_info.copyright_form}")
    private String copyrightForm;
}
