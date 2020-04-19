package blogengine.mappers;

import blogengine.models.Post;
import blogengine.models.Tag;
import blogengine.models.Vote;
import blogengine.models.dto.blogdto.PostDTO;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PostDtoMapper {

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");
    private UserDtoMapper userDtoMapper;
    private CommentDtoMapper commentDtoMapper;

    @Autowired
    public PostDtoMapper(UserDtoMapper userDtoMapper, CommentDtoMapper commentDtoMapper) {
        this.userDtoMapper = userDtoMapper;
        this.commentDtoMapper = commentDtoMapper;
    }

    public PostDTO postToPostDto(Post post){

        PostDTO postDTO = new PostDTO();
        Pair votes = getVoteCount(post);
        postDTO.setId(post.getId());
        postDTO.setTime(dateFormat.format(post.getTime()));
        postDTO.setUser(userDtoMapper.userToUserDto(post.getUser()));
        postDTO.setTitle(post.getTitle());
        postDTO.setAnnounce(getAnnounce(post));
        postDTO.setLikeCount(getLikesCount(votes));
        postDTO.setDislikeCount(getDislikesCount(votes));
        postDTO.setCommentCount(post.getComments().size());
        postDTO.setViewCount(post.getViewCount());
        return postDTO;
    }

    public PostDTO singlePostToPostDto(Post post){

        PostDTO postDTO = postToPostDto(post);
        postDTO.setText(post.getText());
        postDTO.setComments(post.getComments().stream()
                .map(comment -> commentDtoMapper.commentToCommentDto(comment)).collect(Collectors.toList()));
        postDTO.setTags(post.getTags().stream().map(Tag::getName).toArray(String[]::new));
        return postDTO;
    }

    private String getAnnounce(Post post){
        String announce = Jsoup.parse(post.getText()).text();
        announce = (announce.length() > 400) ? announce.substring(0, 400) : announce;
        announce = (announce.matches(".*[.,?!]")) ? announce + ".." : announce + "...";
        return announce;
    }

    private Pair getVoteCount(Post post){
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

    private int getLikesCount(Pair votes){
        return (int) votes.getFirst();
    }

    private int getDislikesCount(Pair votes){
        return (int) votes.getSecond();
    }
}
