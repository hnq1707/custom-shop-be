package org.hnq.ecommerce_be.controller;

import jakarta.validation.Valid;
import org.hnq.ecommerce_be.dto.auth.LoginRequest;
import org.hnq.ecommerce_be.dto.auth.RegisterRequest;
import org.hnq.ecommerce_be.dto.auth.UserDto;
import org.hnq.ecommerce_be.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserDto login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request.getEmail(), request.getPassword());
    }

    @PatchMapping("/change-password")
    public UserDto changePassword(@RequestParam("userId") String userId, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        return userService.changePassword(userId,oldPassword,newPassword);
    }
}
