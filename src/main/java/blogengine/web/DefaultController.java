package blogengine.web;

import blogengine.model.Post;
import blogengine.model.User;
import blogengine.model.Vote;
import blogengine.repository.PostRepository;
import blogengine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/")
public class DefaultController {

}
