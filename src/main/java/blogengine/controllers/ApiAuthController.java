package blogengine.controllers;

import blogengine.models.dto.SuccessDto;
import blogengine.models.dto.requests.LoginRequest;
import blogengine.models.dto.userdto.CaptchaDto;
import blogengine.models.dto.userdto.UserLoginInfo;
import blogengine.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Arrays;
import java.util.Base64;

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

    @GetMapping("captcha")
    public CaptchaDto getCaptcha(){
        CaptchaDto captchaDto = new CaptchaDto();
        captchaDto.setSecret("car4y8cryaw84cr89awnrc");
        String code = Arrays.toString(Base64.getEncoder().encode("tuyoiuo".getBytes()));
        captchaDto.setImage(code);
        return captchaDto;
    }

    @GetMapping("logout")
    public SuccessDto logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return new SuccessDto();
    }
}
