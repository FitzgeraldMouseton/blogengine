package blogengine.controllers;

import blogengine.models.dto.BlogInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class ApiGeneralController {

    @GetMapping("/init")
    public BlogInfo getBlogInfo() {

        return new BlogInfo();
    }
}
