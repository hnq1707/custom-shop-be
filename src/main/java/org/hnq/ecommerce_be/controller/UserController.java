package org.hnq.ecommerce_be.controller;

import org.hnq.ecommerce_be.dto.auth.UserDto;
import org.hnq.ecommerce_be.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDto me(@RequestParam("userId") String userId) {
        return userService.getCurrentUser(userId);
    }
}
