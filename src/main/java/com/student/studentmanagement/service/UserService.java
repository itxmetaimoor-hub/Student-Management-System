package com.student.studentmanagement.service;

import com.student.studentmanagement.config.JwtTokenProvider;
import com.student.studentmanagement.dto.AuthResponse;
import com.student.studentmanagement.dto.UserDTO;
import com.student.studentmanagement.entity.User;
import com.student.studentmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    // Register User
    public UserDTO registerUser(UserDTO dto) {

        User user = new User();
        user.setUsername(dto.getUsername());

        // Encrypt Password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        User savedUser = repository.save(user);

        return new UserDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                "PASSWORD ENCRYPTED",
                savedUser.getRole()
        );
    }

    // Login User
    public AuthResponse loginUser(UserDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        User user = repository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtTokenProvider.generateToken(userDetails);

        return new AuthResponse(token, user.getUsername(), user.getRole());
    }
}
