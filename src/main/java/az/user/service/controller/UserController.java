package az.user.service.controller;

import az.user.service.model.dto.UserDto;
import az.user.service.model.request.UpdateEmailRequest;
import az.user.service.model.request.UpdatePasswordRequest;
import az.user.service.model.request.UpdateUserRequest;
import az.user.service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ms-user")
public class UserController {

    private final UserService userService;

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserDto>> findAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/admin/by-email/{email}")
    public ResponseEntity<UserDto> findUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }

        @GetMapping("/user-info")
    public ResponseEntity<UserDto> findByToken() {
        return ResponseEntity.ok(userService.findUserByToken());
    }

    @PutMapping("/update-user")
    public ResponseEntity<UserDto> update(@RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) {
        return ResponseEntity.ok(userService.updateUserPassword(request));
    }

    @PutMapping("/update-email")
    public ResponseEntity<UserDto> updateEmail(@RequestBody @Valid UpdateEmailRequest request) {
        return ResponseEntity.ok(userService.updateUserEmail(request));
    }
}
