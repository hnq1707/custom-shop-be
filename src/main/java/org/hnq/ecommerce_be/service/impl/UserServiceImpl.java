package org.hnq.ecommerce_be.service.impl;

import org.hnq.ecommerce_be.dto.auth.RegisterRequest;
import org.hnq.ecommerce_be.dto.auth.UserDto;
import org.hnq.ecommerce_be.entity.User;
import org.hnq.ecommerce_be.repository.UserRepository;
import org.hnq.ecommerce_be.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(CONFLICT, "Email already registered");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .role("USER")
                .build();
        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    public UserDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
        }
        return toDto(user);
    }

    @Override
    public UserDto getCurrentUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Not authenticated");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));
        return toDto(user);
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        user.setName(userDto.getName());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    public UserDto changePassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
         userRepository.save(user);
         return toDto(user);
    }

    private static UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .address(u.getAddress())
                .role(u.getRole())
                .build();
    }
}
