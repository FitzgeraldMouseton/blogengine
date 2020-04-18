package blogengine.controllers;

import blogengine.models.dto.requests.LoginRequest;
import blogengine.models.dto.userdto.UserLoginInfo;
import blogengine.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/auth")
public class ApiAuthController {

    private UserService userService;

    @PostMapping("login")
    public UserLoginInfo login(@RequestBody LoginRequest loginRequest) throws UserPrincipalNotFoundException {
        log.info("login");
        return userService.login(loginRequest);
    }

    @GetMapping("check")
    public UserLoginInfo check(){
        return userService.check();
    }
}
