package com.example.stayswift.controller;

import com.example.stayswift.entity.Role;
import com.example.stayswift.entity.User;
import com.example.stayswift.service.UserService;
import com.example.stayswift.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // POST /auth/signup

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> request) {
        String email    = request.get("email");
        String password = request.get("password");
        String roleStr  = request.getOrDefault("role", "USER").toUpperCase();

        Role role;
        try {
            role = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role. Allowed: USER, ADMIN");
        }

        try {
            User user = userService.registerUser(email, password, role);
            return ResponseEntity.ok("Registered successfully: " + user.getEmail());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /auth/login

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email    = request.get("email");
        String password = request.get("password");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        UserDetails userDetails = userService.loadUserByUsername(email);
        String role  = userService.findByEmail(email).get().getRole().name();
        String token = jwtUtil.generateToken(userDetails.getUsername(), role);

        return ResponseEntity.ok(Map.of("token", token));
    }
}