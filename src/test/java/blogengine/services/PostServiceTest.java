package blogengine.services;

import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.User;
import blogengine.repositories.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

//    @Test
//    void findPostById() {
//
//        Post mockPost = new Post(true, ModerationStatus.ACCEPTED, new User(), new User(), LocalDateTime.now(), "Call of Cthulu", "Scary sounds", 56);
//        mockPost.setId(1);
//
//        doReturn(Optional.of(mockPost)).when(postRepository).findById(1);
//
//        Post returnedPost = postService.findPostById(1);
//        log.info(returnedPost.getTitle());
//        log.info(mockPost.getTitle());
//
//        Assertions.assertSame(returnedPost, mockPost, "Posts should be the same");
//    }

    @Test
    void getAllPots() {
    }

//    @Test
//    void save() {
//        Post mockPost = new Post(true, ModerationStatus.ACCEPTED, new User(), new User(), LocalDateTime.now(), "Call of Cthulu", "Scary sounds", 56);
//        mockPost.setId(1);
//
//        postService.save(mockPost);
//    }

    @Test
    void countUserPosts() {
    }

    @Test
    void countUserPostsViews() {
    }

    @Test
    void findFirstPost() {
    }

    @Test
    void findPosts() {
    }

    @Test
    void findCurrentUserPosts() {
    }

    @Test
    void findAllByQuery() {
    }

    @Test
    void findValidPostById() {
    }

    @Test
    void findPostsByDate() {
    }

    @Test
    void findPostsByTag() {
    }

    @Test
    void postsForModeration() {
    }

    @Test
    void addPost() {
    }

    @Test
    void editPost() {
    }

    @Test
    void addComment() {
    }

    @Test
    void likePost() {
    }

    @Test
    void dislikePost() {
    }
}