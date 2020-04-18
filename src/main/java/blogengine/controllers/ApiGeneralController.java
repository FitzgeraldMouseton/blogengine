package blogengine.controllers;

import blogengine.models.dto.BlogInfo;
import blogengine.models.dto.TagDto;
import blogengine.models.dto.settingsDto;
import blogengine.services.TagService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api")
public class ApiGeneralController {

    private TagService tagService;

    @GetMapping("/init")
    public BlogInfo getBlogInfo() {

        return new BlogInfo();
    }

    @GetMapping("/tag")
    public List<TagDto> getTags(String query) {
        return tagService.findTagsByName(query);
    }

    //TODO заглушка, переделать, когда будет авторизация
    @GetMapping("/settings")
    public settingsDto getSettings(){
        return new settingsDto();
    }
}
