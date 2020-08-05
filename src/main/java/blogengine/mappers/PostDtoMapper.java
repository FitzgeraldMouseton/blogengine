package blogengine.mappers;

import blogengine.models.*;
import blogengine.models.dto.blogdto.ModerationResponse;
import blogengine.models.dto.blogdto.postdto.AddPostRequest;
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.models.postconstants.PostConstraints;
import blogengine.services.SettingService;
import blogengine.services.TagService;
import blogengine.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostDtoMapper {

    private final UserDtoMapper userDtoMapper;
    private final CommentDtoMapper commentDtoMapper;
    private final UserService userService;
    private final SettingService settingService;
    private final TagService tagService;

    public PostDto postToPostDto(final Post post) {
        PostDto postDto = new PostDto();
        if (post != null) {
            Pair<Integer, Integer> votes = getVoteCount(post);
            postDto.setId(post.getId());
            postDto.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.UTC));
            postDto.setTitle(post.getTitle());
            postDto.setAnnounce(getAnnounce(post));
            postDto.setLikeCount(getLikesCount(votes));
            postDto.setDislikeCount(getDislikesCount(votes));
            postDto.setCommentCount(post.getComments().size());
            postDto.setViewCount(post.getViewCount());
            postDto.setUser(userDtoMapper.userToUserDto(post.getUser()));
        }
        return postDto;
    }

    public PostDto singlePostToPostDto(final Post post) {
        if (post != null) {
            PostDto postDTO = postToPostDto(post);
            postDTO.setText(post.getText());
            postDTO.setComments(post.getComments().stream()
                    .map(commentDtoMapper::commentToCommentDto).collect(Collectors.toList()));
            postDTO.setTags(post.getTags().stream().map(Tag::getName).toArray(String[]::new));
            return postDTO;
        }
        return null;
    }

    public Post addPostRequestToPost(final AddPostRequest request) {
        Post post = new Post();
        User user = userService.getCurrentUser();
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        LocalDateTime requestTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(request.getTimestamp()), ZoneOffset.UTC);
        LocalDateTime postTime = requestTime
                .isBefore(LocalDateTime.now(ZoneOffset.UTC)) ? LocalDateTime.now(ZoneOffset.UTC) : requestTime;
        post.setTime(postTime);
        post.setActive(request.isActive());
        if (!settingService.isPremoderationEnabled() || user.isModerator()) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        } else {
            post.setModerationStatus(ModerationStatus.NEW);
        }
        Set<Tag> tags = request.getTagNames().stream()
                .map(tagName -> {
                    tagName = tagName.toUpperCase();
                    return tagService.findTagByName(tagName).orElse(new Tag(tagName));
                }).collect(Collectors.toSet());
        post.addTags(tags);
        return post;
    }

    public ModerationResponse postToModerationResponse(final Post post) {
        ModerationResponse response = new ModerationResponse();
        response.setId(post.getId());
        response.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.UTC));
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
