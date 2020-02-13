package blogengine.web;

import blogengine.model.Post;
import blogengine.model.dto.BlogInfo;
import blogengine.model.dto.PostDTO;
import blogengine.model.dto.PostsInfo;
import blogengine.repository.PostRepository;
import blogengine.util.PostMapper;
import org.apache.commons.lang3.reflect.Typed;
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

    private final String isValidConditions = " p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time <= now() ";

    @GetMapping
    public PostsInfo getPosts(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String mode) throws Exception {

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
        List<PostDTO> postDTOs = getPostDTOs(postQuery.getResultList());

        return new PostsInfo(postsCount, postDTOs);
    }

    @GetMapping("search")
    public PostsInfo searchPost(int offset, int limit, String searchQuery) throws Exception {

        TypedQuery<Post> postQuery = entityManager.createQuery("SELECT p FROM Post p WHERE" + isValidConditions + "AND p.text like '%" + searchQuery + "%'", Post.class);
        if (postQuery == null){
            throw new Exception("Query to server is null");
        }
        postQuery.setFirstResult(offset);
        postQuery.setMaxResults(limit);

        long postsCount = postRepository.count();
        List<PostDTO> postDTOs = getPostDTOs(postQuery.getResultList());
        return new PostsInfo(postsCount, postDTOs);
    }

    private List<PostDTO> getPostDTOs(Iterable<Post> posts){
        List<PostDTO> postDTOs = new ArrayList<>();
        posts.forEach(post -> {
            PostDTO postDTO = postMapper.postToPostDTO(post);
            postDTOs.add(postDTO);
        });
        return postDTOs;
    }
}
