package blogengine.controllers;

import blogengine.models.dto.BlogInfo;
import blogengine.models.dto.TagDto;
import blogengine.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api")
public class ApiGeneralController {

    private TagService tagService;

    @Autowired
    public ApiGeneralController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/init")
    public BlogInfo getBlogInfo() {

        return new BlogInfo();
    }

    @GetMapping("/tag")
    public List<TagDto> getTags(String query) {
        return tagService.findTagsByName(query);
    }
}
