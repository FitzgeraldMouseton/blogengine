package blogengine.mappers;

import blogengine.models.Post;
import blogengine.models.Vote;
import blogengine.models.dto.postdto.PostDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class PostDtoMapper {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private UserDtoMapper userDtoMapper;

    @Autowired
    public PostDtoMapper(UserDtoMapper userDtoMapper) {
        this.userDtoMapper = userDtoMapper;
    }

    public PostDTO postToPostDto(Post post){

        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setTime(dateFormat.format(post.getTime()));
        postDTO.setUser(userDtoMapper.userToUserDto(post.getUser()));
        postDTO.setTitle(post.getTitle());
        postDTO.setAnnounce(getAnnounce(post));
        postDTO.setCommentCount(post.getComments().size());
        postDTO.setViewCount(post.getViewCount());
        setVoteCount(post, postDTO);
        return postDTO;
    }

    private String getAnnounce(Post post){

        Document document = Jsoup.parse(post.getText());
        Elements elements = document.select(".Article-Text");
        String announce = "";
        if (elements.size() > 0)
            announce = elements.get(0).selectFirst("strong").text();
        return announce;
    }

    private void setVoteCount(Post post, PostDTO postDTO){
        int likesCount = 0;
        int dislikeCount = 0;
        List<Vote> votes = post.getVotes();
        for (Vote vote: votes){
            if (vote.getValue() == 1)
                likesCount++;
            else
                dislikeCount++;
        }
        postDTO.setLikeCount(likesCount);
        postDTO.setDislikeCount(dislikeCount);
    }
}
