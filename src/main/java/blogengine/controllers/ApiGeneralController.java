package blogengine.controllers;

import blogengine.models.ModerationStatus;
import blogengine.models.User;
import blogengine.models.dto.SettingsDto;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.BlogInfo;
import blogengine.models.dto.blogdto.BlogStatisticsDto;
import blogengine.models.dto.blogdto.CalendarDto;
import blogengine.models.dto.blogdto.TagsResponse;
import blogengine.models.dto.userdto.ChangeProfileRequest;
import blogengine.services.GeneralService;
import blogengine.services.TagService;
import blogengine.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    // ================================ Пока рано ======================================
//    @PostMapping("image")
//    public String uploadImage(@RequestParam MultipartFile image) throws IOException {
//        String string = getPath("uploads/");
//        byte[] bytes = image.getBytes();
//        Path path = Path.of(string);
//        Files.createDirectories(path);
//        string += image.getOriginalFilename();
//        path = Path.of(string);
//        Files.write(path, bytes);
//        return "/" + string;
//    }

//    @PostMapping("profile/my")
//    public SimpleResponseDto my(@RequestBody ChangeProfileRequest request) throws IOException {
//        User user = userService.findById(2);
//        MultipartFile image = request.getPhoto();
//        byte[] bytes = image.getBytes();
//        String string = "avatars";
//        Path path = Path.of(string);
//        Files.createDirectories(path);
//        string += "/1.jpg";
//        Files.write(path, bytes);
//        user.setPhoto(string);
//        return new SimpleResponseDto(true);
//    }

//    private String getPath(String string){
//        int length = 4;
//        int parts = 3;
//        StringBuilder builder = new StringBuilder(string);
//        for (int i = 0; i < parts; i++) {
//            builder.append(RandomStringUtils.randomAlphabetic(length).toLowerCase()).append("/");
//        }
//        return builder.toString();
//    }
}
