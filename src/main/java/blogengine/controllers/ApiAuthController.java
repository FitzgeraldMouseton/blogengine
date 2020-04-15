package blogengine.controllers;

import blogengine.models.dto.requests.LoginRequest;
import blogengine.models.dto.userdto.LoginInfo;
import blogengine.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private UserService userService;

    @Autowired
    public ApiAuthController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/login")
//    public LoginInfo login(@RequestBody LoginRequest loginRequest){
//
//        return userService.login(loginRequest);
//    }
}
