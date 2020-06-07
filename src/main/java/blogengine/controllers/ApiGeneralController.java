package blogengine.controllers;

import blogengine.models.CaptchaCode;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.BlogInfo;
import blogengine.models.dto.blogdto.CalendarDto;
import blogengine.models.dto.blogdto.ModerationRequest;
import blogengine.models.dto.blogdto.StatisticsDto;
import blogengine.models.dto.blogdto.commentdto.CommentRequest;
import blogengine.models.dto.blogdto.commentdto.CommentResponse;
import blogengine.models.dto.blogdto.tagdto.TagsResponse;
import blogengine.models.dto.userdto.ChangeProfileRequest;
import blogengine.services.CaptchaService;
import blogengine.services.GeneralService;
import blogengine.services.PostService;
import blogengine.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ApiGeneralController {

    private final TagService tagService;
    private final GeneralService generalService;
    private final PostService postService;
    private final CaptchaService captchaService;

    @GetMapping("/init")
    public BlogInfo getBlogInfo() {
        return new BlogInfo();
    }

    @PostMapping("moderation")
    public void moderation(@RequestBody ModerationRequest request){
        generalService.moderation(request);
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
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody CommentRequest commentRequest){
        return ResponseEntity.ok().body(postService.addComment(commentRequest));
    }

    @PostMapping("image")
    public String uploadImage(@RequestParam MultipartFile image) throws IOException {
        return generalService.uploadPostImage(image);
    }

    @PostMapping(value = "profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SimpleResponseDto editProfileWithPhoto(@RequestParam MultipartFile photo,
                                                  @ModelAttribute ChangeProfileRequest request) throws IOException {

        return generalService.editProfileWithPhoto(photo, request);
    }

    @PostMapping(value = "profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponseDto editProfile(@RequestBody ChangeProfileRequest request) {
        return generalService.editProfileWithoutPhoto(request);
    }

    @GetMapping("settings")
    public Map<String, Boolean> getSettings(){
        return generalService.getSettings();
    }

    @PutMapping("settings")
    public void changeSettings(@RequestBody Map<String, Boolean> request){
        generalService.changeSettings(request);
    }

    @DeleteMapping
    public void deleteCaptcha(@RequestParam String code){
        CaptchaCode captchaCode = captchaService.findBySecretCode(code);
        captchaService.delete(captchaCode);
    }
}
