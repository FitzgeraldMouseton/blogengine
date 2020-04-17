package blogengine.services;

import blogengine.mappers.PostDtoMapper;
import blogengine.models.ModerationStatus;
import blogengine.models.Post;
import blogengine.models.dto.postdto.PostDTO;
import blogengine.models.dto.postdto.PostsInfo;
import blogengine.repositories.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {

    private PostDtoMapper postDtoMapper;
    private PostRepository postRepository;

    public PostsInfo findPosts(int offset, int limit, String mode) {

        List<Post> posts = null;
        long postsCount = postRepository.countAllByModerationStatusAndTimeBeforeAndActiveTrue(ModerationStatus.ACCEPTED, new Date());
        Pageable pageable = PageRequest.of(offset, limit);
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
        return new PostsInfo(postsCount, postDTOs);
    }

    public PostsInfo findAllByQuery(int offset, int limit, String query){

        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> posts = postRepository.findPostsByQuery(ModerationStatus.ACCEPTED, new Date(), query, pageable);
        List<PostDTO> postDTOs = getPostDTOs(posts);
        return new PostsInfo(posts.size(), postDTOs);
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
