package blogengine.controllers;

import blogengine.exceptions.UserNotFoundException;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.ErrorResponse;
import blogengine.models.dto.blogdto.postdto.AddPostRequest;
import blogengine.models.dto.blogdto.votedto.VoteRequest;
import blogengine.models.dto.blogdto.postdto.PostDto;
import blogengine.models.dto.blogdto.postdto.PostsInfo;
import blogengine.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/post")
public class ApiPostController {

    private final PostService postService;

    @GetMapping
    public PostsInfo getPosts(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String mode) {
        return postService.findPosts(offset, limit, mode);
    }

    @GetMapping("/search")
    public PostsInfo searchPost(@RequestParam int offset, @RequestParam int limit, @RequestParam String query) {
        return postService.findAllByQuery(offset, limit, query);
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable Integer id){
        try {
            return postService.findValidPostById(id);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/byDate")
    public PostsInfo searchByDate(@RequestParam int offset, @RequestParam int limit, @RequestParam String date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateQuery = LocalDate.parse(date, dateFormat);
        return postService.findPostsByDate(offset, limit, dateQuery);
    }

    @GetMapping("/byTag")
    public PostsInfo searchByTag(@RequestParam int offset, @RequestParam int limit, @RequestParam String tag) {
        return postService.findPostsByTag(offset, limit, tag);
    }

    @PostMapping
    public ResponseEntity<?> addPost(@RequestBody AddPostRequest request){
        HashMap<String, String> errors = new HashMap<>();
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(postService.addPost(request));
        } catch (IllegalArgumentException ex){
            if(ex.getLocalizedMessage().equals("Заголовок не установлен")){
                errors.put("title", "Заголовок не установлен");
            } else if (ex.getLocalizedMessage().equals("Текст публикации слишком короткий")){
                errors.put("text", "Текст публикации слишком короткий");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> editPost(@PathVariable int id, @RequestBody AddPostRequest request){
        log.info("trig");
        HashMap<String, String> errors = new HashMap<>();
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(postService.editPost(id, request));
        } catch (IllegalArgumentException ex){
            if(ex.getLocalizedMessage().equals("Заголовок не установлен")){
                errors.put("title", "Заголовок не установлен");
            } else if (ex.getLocalizedMessage().equals("Текст публикации слишком короткий")){
                errors.put("text", "Текст публикации слишком короткий");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }

    @PostMapping("like")
    public SimpleResponseDto addLike(@RequestBody VoteRequest request){
        log.info("like c");
        return postService.likePost(request);
    }

    @PostMapping("dislike")
    public SimpleResponseDto dislikePost(@RequestBody VoteRequest request){
        return postService.dislikePost(request);
    }

    @GetMapping("/my")
    public PostsInfo getCurrentUserPosts(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String status){
        return postService.findCurrentUserPosts(offset, limit, status);
    }

    @GetMapping("moderation")
    public PostsInfo getPostsForModeration(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String status){
        return postService.postsForModeration(offset, limit, status);
    }
}
