package blogengine.controllers;

import blogengine.models.dto.ErrorResponse;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.BlogInfo;
import blogengine.models.dto.blogdto.CalendarDto;
import blogengine.models.dto.blogdto.StatisticsDto;
import blogengine.models.dto.blogdto.commentdto.CommentRequest;
import blogengine.models.dto.blogdto.tagdto.TagsResponse;
import blogengine.models.dto.userdto.ChangeProfileRequest;
import blogengine.services.GeneralService;
import blogengine.services.PostService;
import blogengine.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ApiGeneralController {

    private final TagService tagService;
    private final GeneralService generalService;
    private final PostService postService;

    @GetMapping("/init")
    public BlogInfo getBlogInfo() {

        return new BlogInfo();
    }

    @GetMapping("/tag")
    public TagsResponse getTags(String query) {
        return tagService.findTagsByName(query);
    }

    @GetMapping("statistics/my")
    public StatisticsDto getUserStatistics(){
        return generalService.getCurrentUserStatistics();
    }

    @GetMapping("statistics/all")
    public StatisticsDto getGeneralStatistics(){
        return generalService.getBlogStatistics();
    }

    @GetMapping("calendar")
    public CalendarDto calendar(@RequestParam int year){
        return generalService.calendar(year);
    }

    @PostMapping("comment")
    public ResponseEntity<?> addComment(@RequestBody CommentRequest commentRequest){
        HashMap<String, String> errors = new HashMap<>();
        try {
            return ResponseEntity.ok().body(postService.addComment(commentRequest));
        } catch (IllegalArgumentException ex) {
            if (ex.getLocalizedMessage().equals("Текст комментария не задан или слишком короткий"))
                errors.put("text", "Текст комментария не задан или слишком короткий");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }

    @PostMapping("image")
    public String uploadImage(@RequestParam MultipartFile image) throws IOException {
        return generalService.uploadPostImage(image);
    }

    @PostMapping(value = "profile/my")
    public SimpleResponseDto editProfile(@RequestBody ChangeProfileRequest request) throws IOException {
        return generalService.editProfile(request);
    }

    @GetMapping("settings")
    public Map<String, Boolean> getSettings(){
        return generalService.getSettings();
    }

    @PutMapping("settings")
    public void changeSettings(@RequestBody Map<String, Boolean> request){
        generalService.changeSettings(request);
    }
}
