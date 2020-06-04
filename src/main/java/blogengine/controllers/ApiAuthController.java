package blogengine.controllers;

import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.authdto.*;
import blogengine.services.AuthService;
import blogengine.services.CaptchaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/auth")
public class ApiAuthController {

    private AuthService authService;
    private CaptchaService captchaService;

    @PostMapping("login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("register")
    public ResponseEntity<SimpleResponseDto> register(@RequestBody RegisterRequest registerRequest) throws Exception {
        return ResponseEntity.ok().body(authService.register(registerRequest));
    }

    @GetMapping("check")
    public ResponseEntity<?> check() {
        AuthenticationResponse response = authService.check();
        if (response == null){
            return ResponseEntity.ok().body(new SimpleResponseDto(false));
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("captcha")
    public CaptchaDto getCaptcha(){
        return captchaService.generateCaptcha();
    }

    @PostMapping("restore")
    public SimpleResponseDto restorePassword(@RequestBody RestorePasswordRequest request){
        return authService.sendRestorePasswordMessage(request.getEmail());
    }

    @PostMapping("password")
    public ResponseEntity<SimpleResponseDto> setNewPassword(@RequestBody SetPassRequest setPassDto){
        return ResponseEntity.ok().body(new SimpleResponseDto(true));
    }

    @GetMapping("logout")
    public SimpleResponseDto logout(HttpServletRequest request) throws ServletException {
        return authService.logout(request);
    }
}
