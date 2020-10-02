package blogengine.controllers;

import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.BlogInfo;
import blogengine.models.dto.blogdto.CalendarDto;
import blogengine.models.dto.blogdto.ModerationRequest;
import blogengine.models.dto.blogdto.StatisticsDto;
import blogengine.models.dto.blogdto.commentdto.CommentRequest;
import blogengine.models.dto.blogdto.commentdto.CommentResponse;
import blogengine.models.dto.blogdto.tagdto.TagsResponse;
import blogengine.models.dto.userdto.EditProfileRequest;
import blogengine.services.GeneralService;
import blogengine.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Role;
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

    @GetMapping("/init")
    public BlogInfo getBlogInfo() {
        return BlogInfo.builder().title(title).subtitle(subtitle).phone(phone)
                .email(email).copyright(copyright).copyrightForm(copyrightForm).build();
    }

    @PostMapping("moderation")
    public void moderation(@RequestBody final ModerationRequest request) {
        generalService.moderation(request);
    }

    @GetMapping("/tag")
    public TagsResponse getTags(final String query) {
        return tagService.findTagsByName(query);
    }

    @GetMapping("statistics/my")
    public StatisticsDto getUserStatistics() {
        return generalService.getCurrentUserStatistics();
    }

    @GetMapping("statistics/all")
    public StatisticsDto getGeneralStatistics(){
        return generalService.getBlogStatistics();
    }

    @GetMapping("calendar")
    public CalendarDto calendar(@RequestParam final int year) {
        return generalService.calendar(year);
    }

    @PostMapping("comment")
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody final CommentRequest commentRequest) {
        return ResponseEntity.ok().body(generalService.addComment(commentRequest));
    }

    @PostMapping(value = "image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadImage(@RequestParam(name = "image") final MultipartFile file) throws IOException {
        return generalService.uploadImage(file);
    }

    @PostMapping(value = "profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SimpleResponseDto editProfileWithPhoto(@RequestParam final MultipartFile photo,
                                                  @ModelAttribute @Valid final EditProfileRequest request) throws IOException {

        return generalService.editProfileWithPhoto(photo, request);
    }

    @PostMapping(value = "profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponseDto editProfile(@Valid @RequestBody final EditProfileRequest request) {
        return generalService.editProfileWithoutPhoto(request);
    }

    @GetMapping("settings")
    public Map<String, Boolean> getSettings() {
        return generalService.getSettings();
    }

    @PutMapping("settings")
    public void changeSettings(@RequestBody Map<String, Boolean> request) {
        generalService.changeSettings(request);
    }
}
