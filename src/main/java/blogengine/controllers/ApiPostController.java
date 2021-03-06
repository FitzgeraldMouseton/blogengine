package blogengine.controllers;

import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.blogdto.postdto.AddPostRequest;
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.models.dto.blogdto.postdto.PostsInfoResponse;
import blogengine.models.dto.blogdto.votedto.VoteRequest;
import blogengine.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/post")
public class ApiPostController {

    private final PostService postService;

    @GetMapping
    public PostsInfoResponse<PostDto> getPosts(@RequestParam final int offset,
                                            @RequestParam final int limit,
                                            @RequestParam final String mode) {
        return postService.findPosts(offset, limit, mode);
    }

    @GetMapping("/search")
    public PostsInfoResponse<PostDto> searchPost(@RequestParam final int offset,
                                        @RequestParam final int limit,
                                        @RequestParam final String query) {
        return postService.findAllByQuery(offset, limit, query);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Integer id) {
        final PostDto postDto = postService.getPost(id);
        if (postDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/byDate")
    public PostsInfoResponse<PostDto> searchByDate(@RequestParam final int offset,
                                          @RequestParam final int limit,
                                          @RequestParam final String date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateQuery = LocalDate.parse(date, dateFormat);
        return postService.findPostsByDate(offset, limit, dateQuery);
    }

    @GetMapping("/byTag")
    public PostsInfoResponse<PostDto> searchByTag(@RequestParam final int offset,
                                         @RequestParam final int limit,
                                         @RequestParam final String tag) {
        return postService.findPostsByTag(offset, limit, tag);
    }

    @PostMapping
    public ResponseEntity<?> addPost(@Valid @RequestBody final AddPostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.addPost(request));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> editPost(@PathVariable final int id, @Valid @RequestBody final AddPostRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.editPost(id, request));
    }

    @PostMapping("like")
    public SimpleResponseDto addLike(@RequestBody final VoteRequest request) {
        return postService.likePost(request);
    }

    @PostMapping("dislike")
    public SimpleResponseDto dislikePost(@RequestBody final VoteRequest request) {
        return postService.dislikePost(request);
    }

    @GetMapping("/my")
    public PostsInfoResponse<PostDto> getCurrentUserPosts(@RequestParam final int offset,
                                                 @RequestParam final int limit,
                                                 @RequestParam final String status) {
        return postService.findCurrentUserPosts(offset, limit, status);
    }

    @GetMapping("moderation")
    public PostsInfoResponse getPostsForModeration(@RequestParam final int offset,
                                                   @RequestParam final int limit,
                                                   @RequestParam final String status) {
        return postService.findPostsForModeration(offset, limit, status);
    }
}
