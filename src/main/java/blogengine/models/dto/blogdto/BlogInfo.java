package blogengine.models.dto.blogdto;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Component
public class BlogInfo implements Serializable {

    private final String title = "DevPub";
    private final String subtitle = "Рассказы разработчиков";
    private final String phone = "+7 903 666-44-55";
    private final String email = "mail@mail.ru";
    private final String copyright = "Дмитрий Сергеев";
    private final String copyrightForm = "2005";

    public BlogInfo() {
    }
}
