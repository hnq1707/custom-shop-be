package org.hnq.ecommerce_be.service;

import org.hnq.ecommerce_be.dto.auth.RegisterRequest;
import org.hnq.ecommerce_be.dto.auth.UserDto;

public interface UserService {
    UserDto register(RegisterRequest request);
    UserDto login(String email, String password);
    UserDto getCurrentUser(String userId);

    UserDto update(UserDto user);

    UserDto changePassword(String userId, String oldPassword, String newPassword);
}
