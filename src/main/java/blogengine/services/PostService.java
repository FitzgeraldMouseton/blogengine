package blogengine.services;

import blogengine.mappers.PostDtoMapper;
import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.Tag;
import blogengine.models.User;
import blogengine.models.dto.postdto.PostDTO;
import blogengine.models.dto.postdto.PostsInfo;
import blogengine.models.dto.requests.AddPostRequest;
import blogengine.repositories.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class PostService {

    private PostDtoMapper postDtoMapper;
    private PostRepository postRepository;
    private UserService userService;

    @Autowired
    public PostService(PostDtoMapper postDtoMapper, PostRepository postRepository, UserService userService) {
        this.postDtoMapper = postDtoMapper;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public PostsInfo findPosts(int offset, int limit, String mode) {

        log.trace("hkj");
        List<Post> posts = null;
        log.trace("hkj");
        Pageable pageable = PageRequest.of(offset, limit);

        log.trace("hkj");
        switch (mode) {
            case "recent":
                posts = postRepository.findRecentPosts(ModerationStatus.ACCEPTED, new Date(), pageable);
                break;
            case "early":
                posts = postRepository.findEarlyPosts(ModerationStatus.ACCEPTED, new Date(), pageable);
                break;
            case "popular":
                posts = postRepository.findPopularPosts(ModerationStatus.ACCEPTED, new Date(), pageable);
                break;
            case "best":
                posts = postRepository.findBestPosts(ModerationStatus.ACCEPTED, new Date(), pageable);
                break;
        }

        if (posts == null)
            throw new IllegalArgumentException("Wrong argument 'mode': " + mode);

        List<PostDTO> postDTOs = getPostDTOs(posts);
        return new PostsInfo(posts.size(), postDTOs);
    }

    public PostsInfo findAllByQuery(int offset, int limit, String query){

        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> posts = postRepository.findPostsByQuery(ModerationStatus.ACCEPTED, new Date(), query, pageable);
        List<PostDTO> postDTOs = getPostDTOs(posts);
        return new PostsInfo(posts.size(), postDTOs);
    }

    public PostDTO findValidPostById(int id){
        Optional<Post> postOptional = postRepository.findValidPostById(id, ModerationStatus.ACCEPTED, new Date());
        if (postOptional.isEmpty())
            throw new NoSuchElementException(String.format("Пост с id = %d не найден", id));
        Post post = postOptional.get();
        log.info(post.getTitle());
        log.info(post.getText());
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        return postDtoMapper.singlePostToPostDto(post);
    }

    public PostsInfo findPostsByDate(int offset, int limit, Date date) {
        Pageable pageable = PageRequest.of(offset, limit);
        Date limitDate = DateUtils.addDays(date, 1);
        List<Post> posts = postRepository.findPostsByDate(ModerationStatus.ACCEPTED, new Date(), date, limitDate, pageable);
        return new PostsInfo(posts.size(), getPostDTOs(posts));
    }

    public PostsInfo findPostsByTag(int offset, int limit, String tag) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> posts = postRepository.findAllByTag(ModerationStatus.ACCEPTED, new Date(), tag, pageable);
        return new PostsInfo(posts.size(), getPostDTOs(posts));
    }

    //TODO сделать нормально
    public boolean addPost(AddPostRequest request) throws ParseException {

        User user = userService.findById(10);
        log.info(request.toString());
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
        String date1 = dateFormat.format(date);
        Post post = new Post();
        post.setTime(dateFormat.parse(date1));
        post.setActive(request.getActive());
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        for (Tag tag : request.getTags()) {
            post.getTags().add(tag);
        }
        post.setModerationStatus(ModerationStatus.NEW);
        post.setUser(user);
        post.setModerator(user);
        postRepository.save(post);
        return true;
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
