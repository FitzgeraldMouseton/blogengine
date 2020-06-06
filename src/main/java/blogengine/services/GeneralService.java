package blogengine.services;

import blogengine.models.GlobalSetting;
import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.User;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.CalendarDto;
import blogengine.models.dto.blogdto.ModerationRequest;
import blogengine.models.dto.blogdto.StatisticsDto;
import blogengine.models.dto.userdto.ChangeProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneralService {

    private final PostService postService;
    private final UserService userService;
    private final VoteService voteService;
    private final SettingService settingService;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String POST_IMAGE_LOCATION = "uploads/";
    private static final String USER_AVATAR_LOCATION = "avatars/";

    public void moderation(ModerationRequest request){
        Post post = postService.findPostById(request.getPostId());
        if ("decline".equals(request.getDecision())) {
            post.setModerationStatus(ModerationStatus.DECLINE);
        } else if ("accept".equals(request.getDecision())) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
            postService.save(post);
        }
    }

    public StatisticsDto getCurrentUserStatistics(){

        User user = userService.getCurrentUser();
        Long postsCount = postService.countUserPosts(user);
        log.info(String.valueOf(postsCount));
        Post firstPost = postService.findFirstPost();
        String firstPostDate = dateFormat.format(firstPost.getTime());
        Long likesCount = voteService.countLikesOfUser(user);
        Long dislikesCount = voteService.countDislikesOfUser(user);
        Long viewsCount = postService.countUserPostsViews(user);
        return new StatisticsDto(postsCount, likesCount, dislikesCount, viewsCount, firstPostDate);
    }

    public StatisticsDto getBlogStatistics(){

        List<Post> posts = postService.getAllPots();
        StatisticsDto statisticsDto = new StatisticsDto();
        statisticsDto.setPostsCount(posts.size());
        Post firstPost = postService.findFirstPost();
        statisticsDto.setFirstPublication(dateFormat.format(firstPost.getTime()));
        posts.forEach(post -> {
            post.getVotes().forEach(vote -> {
                if(vote.getValue() == 1)
                    statisticsDto.setLikesCount(statisticsDto.getLikesCount() + 1);
                else if (vote.getValue() == -1)
                    statisticsDto.setDislikesCount(statisticsDto.getDislikesCount() + 1);
            });
            statisticsDto.setViewsCount(statisticsDto.getViewsCount() + post.getViewCount());
        });
        return statisticsDto;
    }

    public CalendarDto calendar(int year){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        CalendarDto calendarDto = new CalendarDto();
        List<Post> posts = postService.getAllPots();

        final Map<String, Long> postsPerDate = posts.stream()
                                                .filter(post -> post.getTime().getYear() == year)
                                                .sorted(Comparator.comparing(Post::getTime).reversed())
                                                .collect(Collectors.groupingBy(p -> dateFormat.format(p.getTime()), Collectors.counting()));

        final Integer[] years = posts.stream().map(post -> post.getTime().getYear()).distinct()
                .sorted(Comparator.reverseOrder()).toArray(Integer[]::new);

        calendarDto.setPosts(postsPerDate);
        calendarDto.setYears(years);
        return calendarDto;
    }

    public Map<String, Boolean> getSettings(){
        Map<String, Boolean> response = new HashMap<>();
        User currentUser = userService.getCurrentUser();
        if (currentUser != null && currentUser.isModerator()){
            List<GlobalSetting> settings = settingService.getSettings();
            if (settings.isEmpty()){
                settingService.fillSettings();
            }
            settings.forEach(setting -> response.put(setting.getCode(), setting.getValue()));
        }
        return response;
    }

    public void changeSettings(Map<String, Boolean> request){
        User currentUser = userService.getCurrentUser();
        if (currentUser != null && currentUser.isModerator()){
            request.keySet().forEach(k -> {
                GlobalSetting setting = settingService.getSettingByCode(k);
                if (setting == null){
                    setting = settingService.setSetting(k, request.get(k));
                } else {
                    setting.setValue(request.get(k));
                }
                settingService.save(setting);
            });
        }
    }

    public SimpleResponseDto editProfileWithPhoto(MultipartFile file, ChangeProfileRequest request) throws IOException {
        User user = userService.getCurrentUser();
        String photo = uploadUserAvatar(file);
        user.setPhoto(photo);
        editProfile(user, request);
        userService.save(user);
        return new SimpleResponseDto(true);
    }

    public SimpleResponseDto editProfileWithoutPhoto(ChangeProfileRequest request) {

        User user = userService.getCurrentUser();
        editProfile(user, request);
        userService.save(user);
        return new SimpleResponseDto(true);
    }

    public String uploadPostImage(MultipartFile image) throws IOException {
        return uploadImage(image, POST_IMAGE_LOCATION);
    }

    private String uploadUserAvatar(MultipartFile image) throws IOException {
        return uploadImage(image, USER_AVATAR_LOCATION);
    }

    private String uploadImage(MultipartFile image, String imageLocation) throws IOException {
        imageLocation = getPathForUpload(imageLocation);
        byte[] bytes = image.getBytes();
        imageLocation += image.getOriginalFilename();
        Path path = Path.of(imageLocation);
        Files.write(path, bytes);
        return "/" + imageLocation;
    }

    private String getPathForUpload(String string) throws IOException {
        int length = 4;
        int parts = 3;
        StringBuilder builder = new StringBuilder(string);
        for (int i = 0; i < parts; i++) {
            String rand = RandomStringUtils.randomAlphabetic(length).toLowerCase();
            builder.append(rand).append("/");
        }
        Path path = Path.of(builder.toString());
        Files.createDirectories(path);
        return builder.toString();
    }

    private void editProfile(User user, ChangeProfileRequest request){
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        String password = request.getPassword();
        if (password != null && password.length() > 5){
            user.setPassword(request.getPassword());
        }
    }
}















//    Map<String, Integer> postsPerDate = posts.stream().filter(post -> post.getTime().getYear() == year)
//            .collect(Collectors.groupingBy(p -> p.getTime().toLocalDate()))
//            .entrySet().stream()
//            .collect(Collectors.toMap(e -> dateFormat.format(e.getKey()), e -> e.getValue().size()))
//            .entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
//            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));