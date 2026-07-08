package com.student.studentmanagement.controller;

import com.student.studentmanagement.dto.AuthResponse;
import com.student.studentmanagement.dto.UserDTO;
import com.student.studentmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;

    // Register
    @PostMapping("/register")
    public UserDTO registerUser(@Valid @RequestBody UserDTO dto) {
        return service.registerUser(dto);
    }

    // Login
    @PostMapping("/login")
    public AuthResponse loginUser(@RequestBody UserDTO dto) {
        return service.loginUser(dto);
    }
}
