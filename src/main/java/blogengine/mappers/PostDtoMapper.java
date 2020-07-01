package blogengine.mappers;

import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.Tag;
import blogengine.models.Vote;
import blogengine.models.dto.blogdto.ModerationResponse;
import blogengine.models.dto.blogdto.postdto.AddPostRequest;
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.models.postconstants.PostConstraints;
import blogengine.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostDtoMapper {

    private final UserDtoMapper userDtoMapper;
    private final CommentDtoMapper commentDtoMapper;
    private final UserService userService;

    private DateTimeFormatter dateFormat;

    public PostDto postToPostDto(final Post post) {
        dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");
        PostDto postDto = new PostDto();
        Pair<Integer, Integer> votes = getVoteCount(post);
        postDto.setId(post.getId());
        postDto.setTime(dateFormat.format(post.getTime()));
        postDto.setTitle(post.getTitle());
        postDto.setAnnounce(getAnnounce(post));
        postDto.setLikeCount(getLikesCount(votes));
        postDto.setDislikeCount(getDislikesCount(votes));
        postDto.setCommentCount(post.getComments().size());
        postDto.setViewCount(post.getViewCount());
        postDto.setUser(userDtoMapper.userToUserDto(post.getUser()));
        return postDto;
    }

    public PostDto singlePostToPostDto(final Post post) {
        PostDto postDTO = postToPostDto(post);
        postDTO.setText(post.getText());
        postDTO.setComments(post.getComments().stream()
                .map(commentDtoMapper::commentToCommentDto).collect(Collectors.toList()));
        postDTO.setTags(post.getTags().stream().map(Tag::getName).toArray(String[]::new));
        return postDTO;
    }

    public Post addPostRequestToPost(final AddPostRequest request) {
        dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Post post = new Post();
        post.setUser(userService.getCurrentUser());
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        LocalDateTime requestTime = LocalDateTime.parse(request.getTime(), dateFormat);
        post.setTime(requestTime);
        post.setActive(request.isActive());
        post.setModerationStatus(ModerationStatus.NEW);
        Set<Tag> tags = new HashSet<>(Arrays.asList(request.getTagNames()));
        post.addTags(tags);
        return post;
    }

    public ModerationResponse postToModerationResponse(final Post post) {
        dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm");
        ModerationResponse response = new ModerationResponse();
        response.setId(post.getId());
        response.setTime(dateFormat.format(post.getTime()));
        response.setUser(userDtoMapper.userToUserDto(post.getUser()));
        response.setTitle(post.getTitle());
        response.setAnnounce(getAnnounce(post));
        return response;
    }

    private String getAnnounce(final Post post) {
        String announce = Jsoup.parse(post.getText()).text();
        announce = (announce.length() > PostConstraints.ANNOUNCE_LENGTH)
                ? announce.substring(0, PostConstraints.ANNOUNCE_LENGTH) : announce;
        announce = (announce.matches(".*[.,?!]")) ? announce + ".." : announce + "...";
        return announce;
    }

    private Pair<Integer, Integer> getVoteCount(final Post post) {
        int likesCount = 0;
        int dislikeCount = 0;
        for (Vote vote: post.getVotes()) {
            if (vote.getValue() == 1)
                likesCount++;
            else
                dislikeCount++;
        }
        return Pair.of(likesCount, dislikeCount);
    }

    private int getLikesCount(final Pair<Integer, Integer> votes) {
        return votes.getFirst();
    }

    private int getDislikesCount(final Pair<Integer, Integer> votes) {
        return votes.getSecond();
    }
}
