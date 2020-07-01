package blogengine.services;

import blogengine.exceptions.authexceptions.NotEnoughPrivilegesException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final int FOLDER_NAME_LENGTH = 4;
    private static final int NUMBER_OF_FOLDER_IN_IMAGE_PATH = 3;

    @Value("${image.crop_width}")
    private int cropWidth;
    @Value("${image.crop_height}")
    private int cropHeight;
    @Value("${password.min_length}")
    private int passwordMinLength;

    public void moderation(final ModerationRequest request) {
        Post post = postService.findPostById(request.getPostId());
        if ("decline".equals(request.getDecision())) {
            post.setModerationStatus(ModerationStatus.DECLINE);
        } else if ("accept".equals(request.getDecision())) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        }
        postService.save(post);
    }

    public StatisticsDto getCurrentUserStatistics() {
        User user = userService.getCurrentUser();
        Long postsCount = postService.countUserPosts(user);
        Post firstPost = postService.findFirstPost();
        String firstPostDate = dateFormat.format(firstPost.getTime());
        Long likesCount = voteService.countLikesOfUser(user);
        Long dislikesCount = voteService.countDislikesOfUser(user);
        Long viewsCount = postService.countUserPostsViews(user);
        return new StatisticsDto(postsCount, likesCount, dislikesCount, viewsCount, firstPostDate);
    }

    public StatisticsDto getBlogStatistics() {

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

    public CalendarDto calendar(final int year) {
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
        if (currentUser != null && currentUser.isModerator()) {
            List<GlobalSetting> settings = settingService.getSettings();
            if (settings.isEmpty()) {
                settingService.fillSettings();
            }
            settings.forEach(setting -> response.put(setting.getCode(), setting.getValue()));
        }
        return response;
    }

    public void changeSettings(final Map<String, Boolean> request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null && currentUser.isModerator()) {
            request.keySet().forEach(k -> {
                GlobalSetting setting = settingService.getSettingByCode(k);
                if (setting == null){
                    setting = settingService.setSetting(k, request.get(k));
                } else {
                    setting.setValue(request.get(k));
                }
                settingService.save(setting);
            });
        } else {
            throw new NotEnoughPrivilegesException("Только модератор может редактировать настройки");
        }
    }

    public SimpleResponseDto editProfileWithPhoto(final MultipartFile file,
                                                  final ChangeProfileRequest request) throws IOException {
        User user = getEditedUser(request);
        String photo = uploadUserAvatar(file);
        user.setPhoto(photo);
        userService.save(user);
        return new SimpleResponseDto(true);
    }

    public SimpleResponseDto editProfileWithoutPhoto(final ChangeProfileRequest request) {
        userService.save(getEditedUser(request));
        return new SimpleResponseDto(true);
    }

    public String uploadPostImage(final MultipartFile image) throws IOException {
        return uploadImage(image, POST_IMAGE_LOCATION);
    }

    private String uploadUserAvatar(final MultipartFile image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        String path = null;
        if (bufferedImage.getHeight() > cropHeight || bufferedImage.getWidth() > cropHeight) {

            BufferedImage preliminaryResizedImage = resizeImage(bufferedImage, cropWidth * 2,
                                    cropHeight * 2, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            BufferedImage resizedImage = resizeImage(preliminaryResizedImage,
                                            cropWidth, cropHeight, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            path = getPathForUpload(USER_AVATAR_LOCATION);
            path = path + image.getOriginalFilename();
            File newFile = new File(path);
            ImageIO.write(resizedImage, "jpg", newFile);
        }
        return "/" + path;
    }

    private String uploadImage(final MultipartFile image, String imageLocation) throws IOException {
        imageLocation = getPathForUpload(imageLocation);
        byte[] bytes = image.getBytes();
        imageLocation += image.getOriginalFilename();
        Path path = Path.of(imageLocation);
        Files.write(path, bytes);
        return "/" + imageLocation;
    }

    private void removeUserAvatar(final User user) {

        String pathToPhoto = user.getPhoto();
        int startIndex = pathToPhoto.indexOf(USER_AVATAR_LOCATION);
        int endIndex = pathToPhoto.indexOf("/", USER_AVATAR_LOCATION.length() + 1);
        Path path = Path.of(pathToPhoto.substring(startIndex, endIndex));
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setPhoto("");
        userService.save(user);
    }

    private String getPathForUpload(final String string) throws IOException {
        StringBuilder builder = new StringBuilder(string);
        for (int i = 0; i < NUMBER_OF_FOLDER_IN_IMAGE_PATH; i++) {
            String rand = RandomStringUtils.randomAlphabetic(FOLDER_NAME_LENGTH).toLowerCase();
            builder.append(rand).append("/");
        }
        Path path = Path.of(builder.toString());
        Files.createDirectories(path);
        return builder.toString();
    }

    private User getEditedUser(final ChangeProfileRequest request) {
        User user = userService.getCurrentUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        String password = request.getPassword();
        if (request.getRemovePhoto() == 1 && !user.getPhoto().isEmpty()) {
            removeUserAvatar(user);
        }
        if (password != null && password.length() >= passwordMinLength) {
            user.setPassword(request.getPassword());
        }
        return user;
    }

    private BufferedImage resizeImage(final BufferedImage sourceImage, final int width,
                                      final int height, final Object renderHint) {
        BufferedImage resizedImage = new BufferedImage(width, height, sourceImage.getType());
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, renderHint);
        graphics2D.drawImage(sourceImage, 0, 0, width, height, null);
        graphics2D.dispose();
        return resizedImage;
    }
}