package blogengine.controllers;

import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.authdto.*;
import blogengine.services.AuthService;
import blogengine.services.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class ApiAuthController {

    private final AuthService authService;
    private final CaptchaService captchaService;

    @PostMapping("login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody final LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("register")
    public ResponseEntity<SimpleResponseDto> register(@Valid @RequestBody final RegisterRequest registerRequest) {
        return ResponseEntity.ok().body(authService.register(registerRequest));
    }

    @GetMapping("check")
    public ResponseEntity<?> check() {
        AuthenticationResponse response = authService.check();
        if (response == null) {
            return ResponseEntity.ok().body(new SimpleResponseDto(false));
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("captcha")
    public CaptchaDto getCaptcha() {
        return captchaService.generateCaptcha();
    }

    @PostMapping("restore")
    public SimpleResponseDto restorePassword(@RequestBody final RestorePasswordRequest request) {
        return authService.sendRestorePasswordMessage(request.getEmail());
    }

    @PostMapping("password")
    public ResponseEntity<SimpleResponseDto> setNewPassword(@Valid @RequestBody final SetPassRequest setPassDto) {
        return ResponseEntity.ok().body(authService.setNewPassword(setPassDto));
    }

    @GetMapping("logout")
    public SimpleResponseDto logout(final HttpServletRequest request) throws ServletException {
        return authService.logout(request);
    }
}
