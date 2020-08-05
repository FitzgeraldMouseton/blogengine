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
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.models.dto.blogdto.postdto.PostsInfoResponse;
import blogengine.models.dto.userdto.EditProfileRequest;
import blogengine.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneralService {

    private final PostService postService;
    private final UserService userService;
    private final VoteService voteService;
    private final SettingService settingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);
    private static final int FOLDER_NAME_LENGTH = 4;
    private static final int NUMBER_OF_FOLDERS_IN_IMAGE_PATH = 3;

    @Value("${image.crop_width}")
    private int cropWidth;
    @Value("${image.crop_height}")
    private int cropHeight;
    @Value("${password.min_length}")
    private int passwordMinLength;
    @Value("${location.images}")
    private String imagesLocation;
    @Value("${location.avatars}")
    private String avatarsLocation;

    @Transactional
    public void moderation(final ModerationRequest request) {
        User moderator = userService.getCurrentUser();
        Post post = postService.findPostById(request.getPostId());
        if ("decline".equals(request.getDecision())) {
            post.setModerationStatus(ModerationStatus.DECLINE);
            post.setModerator(moderator);
        } else if ("accept".equals(request.getDecision())) {
            post.setModerator(moderator);
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        }
        postService.save(post);
    }

    public StatisticsDto getCurrentUserStatistics() {
        User user = userService.getCurrentUser();
        long postsCount = postService.countUserPosts(user);
        Post firstPost = postService.findFirstPostOfUser(user);
        long firstPostDate = firstPost.getTime().toInstant(ZoneOffset.UTC).toEpochMilli();
        long likesCount = voteService.countLikesOfUserPosts(user);
        long dislikesCount = voteService.countDislikesOfUser(user);
        long viewsCount = postService.countUserPostsViews(user);
        return new StatisticsDto(postsCount, likesCount, dislikesCount, viewsCount, firstPostDate);
    }

    public StatisticsDto getBlogStatistics() {
        long postsCount = postService.countActivePosts();
        Post firstPost = postService.findFirstPost();
        long firstPostDate = firstPost.getTime().toInstant(ZoneOffset.UTC).toEpochMilli();
        long likesCount = voteService.countLikes();
        long dislikesCount = voteService.countDislikes();
        long viewsCount = postService.countAllPostsViews();
        return new StatisticsDto(postsCount, likesCount, dislikesCount, viewsCount, firstPostDate);
    }

    public CalendarDto calendar(final int year) {
        dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        CalendarDto calendarDto = new CalendarDto();

        List<Integer> years = postService.findAllYears();

        Map<String, Long> posts = postService.findAllDatesInYear(year).stream()
                .collect(Collectors.groupingBy(p -> dateFormat.format(p), Collectors.counting()));

        calendarDto.setPosts(posts);
        calendarDto.setYears(years.toArray(Integer[]::new));
        return calendarDto;
    }

//    public CalendarDto calendar(final int year) {
//        dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        CalendarDto calendarDto = new CalendarDto();
//        Map<String, Long> posts = new HashMap<>();
//
//        String yearsSql = "select distinct year(time) from posts " +
//                "where is_active = 1 and moderation_status = 'ACCEPTED'";
//
//        String postPerYearSql = "select date(time) as date, count(*) as count from posts " +
//                "where is_active = 1 and moderation_status = 'ACCEPTED' and time like '%" + year + "%' " +
//                "group by date order by date desc";
//
//        List<Integer> years = jdbcTemplate.queryForList(yearsSql, Integer.class);
//
//        List<Map<String, Object>> datesInYear = jdbcTemplate.queryForList(postPerYearSql);
//
//        datesInYear.forEach(dates -> {
//            Date date = (Date) dates.get("date");
//            Long count = (Long) dates.get("count");
//            posts.put(date.toString(), count);
//        });

//        calendarDto.setPosts(posts);
//        calendarDto.setYears(years.toArray(Integer[]::new));
//        return calendarDto;
//    }

    public Map<String, Boolean> getSettings() {
        Map<String, Boolean> response = new HashMap<>();
        List<GlobalSetting> settings = settingService.getSettings();
        if (settings.isEmpty()) {
            settingService.fillSettings();
        }
        settings.forEach(setting -> response.put(setting.getCode(), setting.getValue()));
        return response;
    }

    public void changeSettings(final Map<String, Boolean> request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null && currentUser.isModerator()) {
            request.keySet().forEach(k -> {
                GlobalSetting setting = settingService.getSettingByCode(k);
                if (setting == null) {
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

    @Transactional
    public SimpleResponseDto editProfileWithPhoto(final MultipartFile file,
                                                  final EditProfileRequest request) throws IOException {
        User user = getEditedUser(request);
        String photo = uploadUserAvatar(file);
        user.setPhoto(photo);
        userService.save(user);
        return new SimpleResponseDto(true);
    }

    @Transactional
    public SimpleResponseDto editProfileWithoutPhoto(final EditProfileRequest request) {
        userService.save(getEditedUser(request));
        return new SimpleResponseDto(true);
    }

    public String uploadPostImage(final MultipartFile image) throws IOException {
        return uploadImage(image, imagesLocation);
    }

    // ================================== Additional methods =========================================

    private String uploadUserAvatar(final MultipartFile image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        String path = getPathForUpload(avatarsLocation);
        path += image.getOriginalFilename();
        File newFile = new File(path);
        if (bufferedImage.getHeight() > cropHeight || bufferedImage.getWidth() > cropHeight) {

            BufferedImage preliminaryResizedImage = resizeImage(bufferedImage, cropWidth * 2,
                                    cropHeight * 2, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            bufferedImage = resizeImage(preliminaryResizedImage,
                                            cropWidth, cropHeight, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }
        ImageIO.write(bufferedImage, "jpg", newFile);
        return "/" + path;
    }

    private String uploadImage(final MultipartFile image, final String imagesRootFolder) throws IOException {

        String pathToImage = getPathForUpload(imagesRootFolder);
        pathToImage += image.getOriginalFilename();
        image.transferTo(Path.of(pathToImage));
        return "/" + pathToImage;
    }

    private void removeUserAvatar(final User user) {

        String pathToPhoto = user.getPhoto();
        int startIndex = pathToPhoto.indexOf(avatarsLocation);
        int endIndex = pathToPhoto.indexOf("/", avatarsLocation.length() + 1);
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
        user.setPhoto(null);
        userService.save(user);
    }

    private String getPathForUpload(final String string) throws IOException {
        StringBuilder builder = new StringBuilder(string);
        for (int i = 0; i < NUMBER_OF_FOLDERS_IN_IMAGE_PATH; i++) {
            String rand = RandomStringUtils.randomAlphabetic(FOLDER_NAME_LENGTH).toLowerCase();
            builder.append(rand).append("/");
        }
        Path path = Path.of(builder.toString());
        Files.createDirectories(path);
        return builder.toString();
    }

    private User getEditedUser(final EditProfileRequest request) {
        User user = userService.getCurrentUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        String password = request.getPassword();
        if (request.getRemovePhoto() == 1 && user.getPhoto() != null) {
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








// calendar

//    List<Post> posts = postService.findAllActivePosts();
//
//    final Map<String, Long> postsPerDate = posts.stream()
//            .filter(post -> post.getTime().getYear() == year)
//            .sorted(Comparator.comparing(Post::getTime).reversed())
//            .collect(Collectors.groupingBy(p -> dateFormat.format(p.getTime()), Collectors.counting()));
//
//    final Integer[] years = posts.stream().map(post -> post.getTime().getYear()).distinct()
//            .sorted(Comparator.reverseOrder()).toArray(Integer[]::new);

