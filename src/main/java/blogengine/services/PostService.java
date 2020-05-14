package blogengine.services;

import blogengine.mappers.PostDtoMapper;
import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.dto.blogdto.PostDTO;
import blogengine.models.dto.blogdto.PostsInfo;
import blogengine.repositories.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {

    private PostDtoMapper postDtoMapper;
    private PostRepository postRepository;

    public Post findPostById(Integer id){
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> getAllPots(){
        return postRepository.findAllBy();
    }

    public void save(Post post){
        postRepository.save(post);
    }

    public PostsInfo findPosts(int offset, int limit, String mode) {

        List<Post> posts = null;
        long postsCount = postRepository.countAllByModerationStatusAndTimeBeforeAndActiveTrue(ModerationStatus.ACCEPTED, LocalDateTime.now());
        Pageable pageable = PageRequest.of(offset/limit, limit);
        switch (mode) {
            case "recent":
                posts = postRepository.findRecentPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);
                break;
            case "early":
                posts = postRepository.findEarlyPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);
                break;
            case "popular":
                posts = postRepository.findPopularPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);
                break;
            case "best":
                posts = postRepository.findBestPosts(ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);
                break;
        }

        if (posts == null)
            throw new IllegalArgumentException("Wrong argument 'mode': " + mode);

        List<PostDTO> postDTOs = getPostDTOs(posts);
        return new PostsInfo(postsCount, postDTOs);
    }

    public PostsInfo findAllByQuery(int offset, int limit, String query){

        Pageable pageable = PageRequest.of(offset/limit, limit);
        List<Post> posts = postRepository.findPostsByQuery(ModerationStatus.ACCEPTED, LocalDateTime.now(), query, pageable);
        List<PostDTO> postDTOs = getPostDTOs(posts);
        return new PostsInfo(posts.size(), postDTOs);
    }

    public PostDTO findValidPostById(int id){
        Optional<Post> postOptional = postRepository.findValidPostById(id, ModerationStatus.ACCEPTED, LocalDateTime.now());
        if (postOptional.isEmpty())
            throw new NoSuchElementException(String.format("Пост с id = %d не найден", id));
        Post post = postOptional.get();
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        return postDtoMapper.singlePostToPostDto(post);
    }

    public PostsInfo findPostsByDate(int offset, int limit, LocalDate date) {
        Pageable pageable = PageRequest.of(offset/limit, limit);
        List<Post> posts = postRepository.findPostsByDate(ModerationStatus.ACCEPTED, date.atStartOfDay(), date.atStartOfDay().plusDays(1), pageable);
        return new PostsInfo(posts.size(), getPostDTOs(posts));
    }

    public PostsInfo findPostsByTag(int offset, int limit, String tag) {
        Pageable pageable = PageRequest.of(offset/limit, limit);
        List<Post> posts = postRepository.findAllByTag(ModerationStatus.ACCEPTED, LocalDateTime.now(), tag, pageable);
        return new PostsInfo(posts.size(), getPostDTOs(posts));
    }

    private List<PostDTO> getPostDTOs(Iterable<Post> posts){
        List<PostDTO> postDTOs = new ArrayList<>();
        posts.forEach(post -> {
            PostDTO postDTO = postDtoMapper.postToPostDto(post);
            postDTOs.add(postDTO);
        });
        return postDTOs;
    }
}
