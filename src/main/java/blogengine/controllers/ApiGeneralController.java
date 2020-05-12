package blogengine.controllers;

import blogengine.models.dto.SettingsDto;
import blogengine.models.dto.blogdto.BlogInfo;
import blogengine.models.dto.blogdto.BlogStatisticsDto;
import blogengine.models.dto.blogdto.CalendarDto;
import blogengine.models.dto.blogdto.TagsResponse;
import blogengine.services.GeneralService;
import blogengine.services.PostService;
import blogengine.services.TagService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api")
public class ApiGeneralController {

    private TagService tagService;
    private GeneralService generalService;

    @GetMapping("/init")
    public BlogInfo getBlogInfo() {

        return new BlogInfo();
    }

    @GetMapping("/tag")
    public TagsResponse getTags(String query) {
        return tagService.findTagsByName(query);
    }

    @GetMapping("statistics/all")
    public BlogStatisticsDto getGeneralStatistics(){
        return generalService.getBlogStatistics();
    }

    //TODO заглушка, переделать, когда будет авторизация
    @GetMapping("/settings")
    public SettingsDto getSettings(){
        return new SettingsDto();
    }

    @GetMapping("calendar")
    public CalendarDto calendar(@RequestParam int year){
        return generalService.calendar(year);
    }
}
