package blogengine.web;

import blogengine.model.Post;
import blogengine.model.dto.BlogInfo;
import blogengine.model.dto.PostDTO;
import blogengine.model.dto.PostsInfo;
import blogengine.repository.PostRepository;
import blogengine.util.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/post")
public class ApiPostController {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostMapper postMapper;

    @GetMapping
    public PostsInfo getPosts(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String mode) throws Exception {

        String isValidConditions = " p.isActive = 0 and p.moderationStatus = 'ACCEPTED' and p.time <= now() ";
        TypedQuery<Post> postQuery = null;
        switch (mode) {
            case "best":
                postQuery = entityManager.createQuery("SELECT p FROM Post p JOIN p.votes v WHERE v.value=1 AND" + isValidConditions + "GROUP BY p.id ORDER BY sum(v.value) DESC", Post.class);
                break;
            case "recent":
                postQuery = entityManager.createQuery("SELECT p FROM Post p WHERE" + isValidConditions + "ORDER BY p.time DESC", Post.class);
                break;
            case "popular":
                postQuery = entityManager.createQuery("SELECT p FROM Post p JOIN p.comments c WHERE" + isValidConditions + "GROUP BY p.id ORDER BY count(c.id) DESC", Post.class);
                break;
            case "early":
                postQuery = entityManager.createQuery("SELECT p FROM Post p WHERE" + isValidConditions + "ORDER BY p.time ASC", Post.class);
                break;
        }
        if (postQuery == null){
            throw new Exception("Query to server is null");
        }
        postQuery.setFirstResult(offset);
        postQuery.setMaxResults(limit);

        long postsCount = postRepository.count();
        Iterable<Post> posts = postQuery.getResultList();
        List<PostDTO> postSummaries = new ArrayList<>();
        posts.forEach(post -> {
            PostDTO postSummary = postMapper.postToPostSummary(post);
            postSummaries.add(postSummary);
        });

        return new PostsInfo(postsCount, postSummaries);
    }

    @GetMapping("/api/post/search")
    public PostsInfo searchPost(String query){
        Iterable<Post> posts = postRepository.findAll();
        List<PostDTO> postDTOS = new ArrayList<>();
        posts.forEach(post -> {
            if (post.getText().contains(query)){
                PostDTO postDTO = postMapper.postToPostSummary(post);
                postDTOS.add(postDTO);
            }
        });
        int postsCount = postDTOS.size();
        return new PostsInfo(postsCount, postDTOS);
    }
}
