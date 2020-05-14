package blogengine.controllers;

import blogengine.models.dto.blogdto.PostDTO;
import blogengine.models.dto.blogdto.PostsInfo;
import blogengine.services.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("api/post")
@AllArgsConstructor
public class ApiPostController {

    private PostService postService;

    @GetMapping
    public PostsInfo getPosts(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String mode) {
        return postService.findPosts(offset, limit, mode);
    }

    @GetMapping("/search")
    public PostsInfo searchPost(@RequestParam int offset, @RequestParam int limit, @RequestParam String query) {
        return postService.findAllByQuery(offset, limit, query);
    }

    @GetMapping("/{id}")
    public PostDTO getPostById(@PathVariable Integer id){
        return postService.findValidPostById(id);
    }

    @GetMapping("/byDate")
    public PostsInfo searchByDate(@RequestParam int offset, @RequestParam int limit, @RequestParam String date) {
        log.info(date);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateQuery = LocalDate.parse(date, dateFormat);
        return postService.findPostsByDate(offset, limit, dateQuery);
    }

    @GetMapping("/byTag")
    public PostsInfo searchByTag(@RequestParam int offset, @RequestParam int limit, @RequestParam String tag) {
        return postService.findPostsByTag(offset, limit, tag);
    }
}
