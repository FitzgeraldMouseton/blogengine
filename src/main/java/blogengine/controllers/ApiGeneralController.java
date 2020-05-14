package blogengine.controllers;

import blogengine.models.User;
import blogengine.models.dto.SettingsDto;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.BlogInfo;
import blogengine.models.dto.blogdto.BlogStatisticsDto;
import blogengine.models.dto.blogdto.CalendarDto;
import blogengine.models.dto.blogdto.TagsResponse;
import blogengine.models.dto.userdto.ChangeProfileRequest;
import blogengine.services.GeneralService;
import blogengine.services.PostService;
import blogengine.services.TagService;
import blogengine.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api")
public class ApiGeneralController {

    private TagService tagService;
    private GeneralService generalService;
    private UserService userService;

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
