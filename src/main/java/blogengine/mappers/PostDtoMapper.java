package blogengine.mappers;

import blogengine.models.Post;
import blogengine.models.Tag;
import blogengine.models.Vote;
import blogengine.models.dto.blogdto.ModerationResponse;
import blogengine.models.dto.blogdto.postdto.AddPostRequest;
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostDtoMapper {

    private DateTimeFormatter dateFormat;
    private final UserDtoMapper userDtoMapper;
    private final CommentDtoMapper commentDtoMapper;
    private final TagService tagService;

    public PostDto postToPostDto(Post post){
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

    public PostDto singlePostToPostDto(Post post){
        PostDto postDTO = postToPostDto(post);
        postDTO.setText(post.getText());
        postDTO.setComments(post.getComments().stream()
                .map(commentDtoMapper::commentToCommentDto).collect(Collectors.toList()));
        postDTO.setTags(post.getTags().stream().map(Tag::getName).toArray(String[]::new));
        return postDTO;
    }

    public void addPostRequestToPost(AddPostRequest request, Post post){
        dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        LocalDateTime requestTime = LocalDateTime.parse(request.getTime(), dateFormat);
        post.setTime(requestTime);
        post.setActive(request.isActive());
        Set<Tag> tags = request.getTagNames().stream()
                .map(tagName -> tagService.findTagByName(tagName).orElse(new Tag(tagName))).collect(Collectors.toSet());
        post.addTags(tags);
    }

    public ModerationResponse postToModerationResponse(Post post){
        dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm");
        ModerationResponse response = new ModerationResponse();
        response.setId(post.getId());
        response.setTime(dateFormat.format(post.getTime()));
        response.setUser(userDtoMapper.userToUserDto(post.getUser()));
        response.setTitle(post.getTitle());
        response.setAnnounce(getAnnounce(post));
        return response;
    }

    private String getAnnounce(Post post){
        String announce = Jsoup.parse(post.getText()).text();
        announce = (announce.length() > 400) ? announce.substring(0, 400) : announce;
        announce = (announce.matches(".*[.,?!]")) ? announce + ".." : announce + "...";
        return announce;
    }

    private Pair<Integer, Integer> getVoteCount(Post post){
        int likesCount = 0;
        int dislikeCount = 0;
        for (Vote vote: post.getVotes()){
            if (vote.getValue() == 1)
                likesCount++;
            else
                dislikeCount++;
        }
        return Pair.of(likesCount, dislikeCount);
    }

    private int getLikesCount(Pair<Integer, Integer> votes){
        return votes.getFirst();
    }

    private int getDislikesCount(Pair<Integer, Integer> votes){
        return votes.getSecond();
    }
}
