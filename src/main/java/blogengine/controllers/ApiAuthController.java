package blogengine.controllers;

import blogengine.exceptions.IncorrectCaptchaCodeException;
import blogengine.exceptions.UserAlreadyExistsException;
import blogengine.exceptions.UserNotFoundException;
import blogengine.models.dto.ErrorResponse;
import blogengine.models.dto.SimpleResponseDto;
import blogengine.models.dto.authdto.*;
import blogengine.services.AuthService;
import blogengine.services.CaptchaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/auth")
public class ApiAuthController {

    private AuthService authService;
    private CaptchaService captchaService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        HashMap<String, String> errors = new HashMap<>();
        AuthenticationResponse response;
        try {
            response = authService.login(loginRequest);
        } catch (UserNotFoundException ex) {
            errors.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
        }
        if (response == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponseDto(false));
        else
            return ResponseEntity.ok(response);
    }

    @PostMapping("register")
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest) throws Exception {
        HashMap<String, String> errors = new HashMap<>();
        try {
            return ResponseEntity.ok().body(authService.register(registerRequest));
        } catch (IncorrectCaptchaCodeException ex) {
            errors.put("captcha", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            errors.put("name", ex.getMessage());
        } catch (UserAlreadyExistsException ex){
            errors.put("email", ex.getMessage());
        }
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
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
    public ResponseEntity setNewPassword(@RequestBody SetPassRequest setPassDto){
        HashMap<String, String> errors = new HashMap<>();
        try {
            if (authService.setNewPassword(setPassDto)){
                return ResponseEntity.ok().body(new SimpleResponseDto(true));
            }
        } catch (IllegalArgumentException ex) {
            errors.put("captcha", ex.getMessage());
        } catch (IllegalStateException ex) {
            errors.put("code", ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }

    @GetMapping("logout")
    public SimpleResponseDto logout(HttpServletRequest request) throws ServletException {
        return authService.logout(request);
    }
}
