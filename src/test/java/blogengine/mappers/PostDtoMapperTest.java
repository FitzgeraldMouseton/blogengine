package blogengine.mappers;

import blogengine.models.*;
import blogengine.models.Tag;
import blogengine.models.dto.blogdto.commentdto.CommentDTO;
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.models.dto.userdto.UserDto;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
@SpringBootTest
class PostDtoMapperTest {

    @Autowired
    private PostDtoMapper mapper;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    private final LocalDateTime time = LocalDateTime.of(2020, 6, 30, 12, 33);
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");

    private final String text = "The most merciful thing in the world, I think, is the inability of the human mind to correlate all its contents. We live on a placid island of ignorance in the midst of black seas of infinity, and it was not meant that we should voyage far. The sciences, each straining in its own direction, have hitherto harmed us little; but some day the piecing together of dissociated knowledge will open up such terrifying vistas of reality, and of our frightful position therein, that we shall either go mad from the revelation or flee from the deadly light into the peace and safety of a new dark age.";

    private final Tag tag1 = new Tag("Tag1");
    private final Tag tag2 = new Tag("Tag2");

    private final Comment comment1 = new Comment("Comment1");
    private final Comment comment2 = new Comment("Comment2");

    private final Vote like1 = new Vote((byte) 1);
    private final Vote like2 = new Vote((byte) 1);
    private final Vote dislike1 = new Vote((byte) -1);

    private final User user = new User();
    private final User moderator = new User();
    private final User commentator = new User();

    private Post post = null;

    @BeforeEach
    void init() {

        user.setName("Howard F. Lovecraft");
        user.setPhoto("/photos/hfl");

        comment1.setTime(LocalDateTime.now());
        comment1.setUser(commentator);
        comment2.setTime(LocalDateTime.now());
        comment2.setUser(commentator);

        post = Post.builder()
                .active(true)
                .moderationStatus(ModerationStatus.ACCEPTED)
                .user(user)
                .moderator(moderator)
                .time(time)
                .title("Call of Cthulu")
                .text(text).build();
        post.setTags(Set.of(tag1, tag2));
        post.setViewCount(58);
        post.setVotes(List.of(like1, like2, dislike1));
        post.setComments(List.of(comment1, comment2));
    }

    @Test
    void postToPostDto() {

        Post post = Post.builder()
                .active(true)
                .moderationStatus(ModerationStatus.ACCEPTED)
                .user(user)
                .moderator(moderator)
                .time(time)
                .title("Call of Cthulu")
                .text(text).build();
        post.setTags(Set.of(tag1, tag2));
        post.setViewCount(58);
        post.setVotes(List.of(like1, like2, dislike1));
        post.setComments(List.of(comment1, comment2));

        String announce = "The most merciful thing in the world, I think, is the inability of the human mind to correlate all its contents. We live on a placid island of ignorance in the midst of black seas of infinity, and it ...";

        PostDto expectedDto = PostDto.builder()
                .timestamp(time.toEpochSecond(ZoneOffset.UTC))
                .user(new UserDto(0, "Howard F. Lovecraft", "/photos/hfl"))
                .title("Call of Cthulu")
                .announce(announce)
                .likeCount(2)
                .dislikeCount(1)
                .viewCount(58)
                .commentCount(2)
                .build();

        PostDto actualDto = mapper.postToPostDto(post);

        Assertions.assertEquals(expectedDto, actualDto);

    }

    @Test
    void singlePostToPostDto() {

        String announce = "The most merciful thing in the world, I think, is the inability of the human mind to correlate all its contents. We live on a placid island of ignorance in the midst of black seas of infinity, and it ...";

        String[] tagStrings = {"Tag1", "Tag2"};

        CommentDTO commentDTO1 = commentDtoMapper.commentToCommentDto(comment1);
        CommentDTO commentDTO2 = commentDtoMapper.commentToCommentDto(comment2);

        PostDto expectedDto = PostDto.builder()
                .timestamp(time.toEpochSecond(ZoneOffset.UTC))
                .user(new UserDto(0, "Howard F. Lovecraft", "/photos/hfl"))
                .title("Call of Cthulu")
                .announce(announce)
                .text(text)
                .likeCount(2)
                .dislikeCount(1)
                .viewCount(58)
                .commentCount(2)
                .comments(List.of(commentDTO1, commentDTO2))
                .tags(tagStrings)
                .build();

        PostDto actualDto = mapper.singlePostToPostDto(post);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void addPostRequestToPost() {
    }

    @Test
    void postToModerationResponse() {
    }
}