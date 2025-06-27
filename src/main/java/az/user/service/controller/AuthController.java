package az.user.service.controller;

import az.user.service.model.dto.AuthDto;
import az.user.service.model.request.LoginUserRequest;
import az.user.service.model.request.RegisterUserRequest;
import az.user.service.model.request.VerifyOtpRequest;
import az.user.service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/hello")
    public String getMessage(HttpServletRequest request) {
        return "<h1>Hello World!</h1>" + " " + request.getSession().getId();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto> register(@RequestBody @Valid RegisterUserRequest request) {
        log.info("Register user request: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerUser(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String > verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginUserRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

