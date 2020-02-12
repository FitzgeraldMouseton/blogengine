package blogengine.util;

import blogengine.model.Post;
import blogengine.model.Vote;
import blogengine.model.dto.PostDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mappings({
    @Mapping(target = "time", source = "post.time", dateFormat = "MM-dd-yyyy HH:mm"),
    })
    PostDTO postToPostSummary(Post post);

    Post postSummaryToPost(PostDTO postSummary);

    @BeforeMapping
    default void setVoteCount(Post post, @MappingTarget PostDTO postSummary){
        int likesCount = 0;
        int dislikeCount = 0;
        List<Vote> votes = post.getVotes();
        for (Vote vote: votes){
            if (vote.getValue() == 1)
                likesCount++;
            else
                dislikeCount++;
        }
        postSummary.setLikeCount(likesCount);
        postSummary.setDislikeCount(dislikeCount);
    }

    @BeforeMapping
    default void setCommentsCount(Post post, @MappingTarget PostDTO postSummary){
        postSummary.setCommentCount( post.getComments().size());
    }

    @BeforeMapping
    default void setAnnounce(Post post, @MappingTarget PostDTO postSummary){

        Document document = Jsoup.parse(post.getText());
        Elements elements = document.select(".Article-Text");
        String announce = "";
        if (elements.size() > 0)
            announce = elements.get(0).selectFirst("strong").text();
        postSummary.setAnnounce(announce);
    }
}
