package com.main.EMS_backend.controller;

import com.main.EMS_backend.jwt.JwtUtil;
import com.main.EMS_backend.dto.LoginRequest;
import com.main.EMS_backend.entity.User;
import com.main.EMS_backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        user.setRole("USER");
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(request.getEmail());


        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", authentication.getAuthorities()
                .iterator().next().getAuthority());

        System.out.println("Login Successfully");
        return ResponseEntity.ok(response);
    }
}
