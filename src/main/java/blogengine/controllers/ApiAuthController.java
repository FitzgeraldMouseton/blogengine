package blogengine.controllers;

import blogengine.models.dto.LogoutDto;
import blogengine.models.dto.requests.LoginRequest;
import blogengine.models.dto.userdto.UserLoginInfo;
import blogengine.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@RestController
@RequestMapping("api/auth")
public class ApiAuthController {

    private UserService userService;

    @Autowired
    public ApiAuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("login")
    public UserLoginInfo login(@RequestBody LoginRequest loginRequest){

        log.info("trig");
        return userService.login(loginRequest);
    }

    @GetMapping("check")
    public UserLoginInfo check(){
        return userService.check();
    }

    @GetMapping("logout")
    public LogoutDto logout(){
        log.info("trig logout");
        LogoutDto dto = new LogoutDto();
        log.info(dto.toString());
        return dto;
    }
}
