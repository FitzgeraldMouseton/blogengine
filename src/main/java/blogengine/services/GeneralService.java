package blogengine.services;

import blogengine.models.GlobalSetting;
import blogengine.models.Post;
import blogengine.models.User;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.CalendarDto;
import blogengine.models.dto.blogdto.StatisticsDto;
import blogengine.models.dto.userdto.ChangeProfileRequest;
import blogengine.repositories.GlobalSettingsRepository;
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
    private final GlobalSettingsRepository globalSettingsRepository;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String POST_IMAGE_LOCATION = "uploads/";
    private static final String USER_AVATAR_LOCATION = "avatars/";

    public StatisticsDto getCurrentUserStatistics(){

        User user = userService.getCurrentUser();
        Long postsCount = postService.countUserPosts(user);
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

        Map<String, Integer> postsPerDate = posts.stream().filter(post -> post.getTime().getYear() == year)
                .collect(Collectors.groupingBy(p -> p.getTime().toLocalDate()))
                .entrySet().stream()
                .collect(Collectors.toMap(e -> dateFormat.format(e.getKey()), e -> e.getValue().size()))
                .entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        List<Integer> collect = posts.stream().map(post -> post.getTime().getYear()).distinct()
                .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        Integer[] years = new Integer[collect.size()];
        years = collect.toArray(years);

        calendarDto.setPosts(postsPerDate);
        calendarDto.setYears(years);
        return calendarDto;
    }

    public Map<String, Boolean> getSettings(){
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isModerator())
            return null;
        Iterable<GlobalSetting> settings = globalSettingsRepository.findAll();
        Map<String, Boolean> response = new HashMap<>();
        settings.forEach(setting -> response.put(setting.getName(), setting.getValue()));
        return response;
    }

    public void changeSettings(Map<String, Boolean> request){
        User user = userService.getCurrentUser();
        if (user.isModerator()){
            log.info("trig");
            Iterable<GlobalSetting> settings = globalSettingsRepository.findAll();
            settings.forEach(setting -> {
                Boolean value = request.get(setting.getCode());
                if (value != null){
                    setting.setValue(value);
                    globalSettingsRepository.save(setting);
                }
            });
        }
    }

    public SimpleResponseDto editProfile(ChangeProfileRequest request) throws IOException {

        User user = userService.getCurrentUser();
        //String photos = uploadUserAvatar(request.getPhoto());
        //user.setPhoto(photos);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        String password = request.getPassword();
        if (!password.isEmpty() && password.length() > 5){
            user.setPassword(request.getPassword());
        }
        userService.save(user);
        return new SimpleResponseDto(true);
    }

    public String uploadPostImage(MultipartFile image) throws IOException {
        return uploadImage(image, POST_IMAGE_LOCATION);
    }

    private String uploadUserAvatar(MultipartFile image) throws IOException {
        return uploadImage(image, POST_IMAGE_LOCATION);
    }

    private String uploadImage(MultipartFile image, String imageLocation) throws IOException {
        imageLocation = getPath(imageLocation);
        byte[] bytes = image.getBytes();
        imageLocation += image.getOriginalFilename();
        Path path = Path.of(imageLocation);
        Files.write(path, bytes);
        return imageLocation;
    }

    private String getPath(String string) throws IOException {
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
}
