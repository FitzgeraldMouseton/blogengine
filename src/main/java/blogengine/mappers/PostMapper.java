//package blogengine.mappers;
//
//import blogengine.models.Post;
//import blogengine.models.Vote;
//import blogengine.models.dto.postdto.PostDTO;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.select.Elements;
//import org.mapstruct.*;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@Mapper(componentModel = "spring")
//public interface PostMapper {
//
//    @Mappings({
//    @Mapping(target = "time", source = "post.time", dateFormat = "MM-dd-yyyy HH:mm"),
//    })
//    PostDTO postToPostDTO(Post post);
//
//    @BeforeMapping
//    default void setVoteCount(Post post, @MappingTarget PostDTO postDTO){
//        int likesCount = 0;
//        int dislikeCount = 0;
//        List<Vote> votes = post.getVotes();
//        for (Vote vote: votes){
//            if (vote.getValue() == 1)
//                likesCount++;
//            else
//                dislikeCount++;
//        }
//        postDTO.setLikeCount(likesCount);
//        postDTO.setDislikeCount(dislikeCount);
//        postDTO.setViewCount(post.getViewCount());
//    }
//
//    @BeforeMapping
//    default void setCommentsCount(Post post, @MappingTarget PostDTO postDTO){
//        postDTO.setCommentCount( post.getComments().size());
//    }
//
//    @BeforeMapping
//    default void setAnnounce(Post post, @MappingTarget PostDTO postDTO){
//
//        Document document = Jsoup.parse(post.getText());
//        Elements elements = document.select(".Article-Text");
//        String announce = "";
//        if (elements.size() > 0)
//            announce = elements.get(0).selectFirst("strong").text();
//        postDTO.setAnnounce(announce);
//    }
//}
