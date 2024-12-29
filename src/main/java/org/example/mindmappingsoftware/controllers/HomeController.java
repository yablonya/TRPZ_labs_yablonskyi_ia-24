package org.example.mindmappingsoftware.controllers;

import org.example.mindmappingsoftware.dto.UserLoginRequest;
import org.example.mindmappingsoftware.dto.UserRegistrationRequest;
import org.example.mindmappingsoftware.models.User;
import org.example.mindmappingsoftware.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class HomeController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            User registeredUser = userService.registerUser(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword()
            );
            HttpHeaders newHeaders = userService.addUserIdToCookie(registeredUser);

            logger.info("User registered with email: {}", registeredUser.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).headers(newHeaders).body(registeredUser);
        } catch (IllegalArgumentException e) {
            logger.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody UserLoginRequest request
    ) {
        try {
            User user = userService.loginUser(request.getEmail(), request.getPassword());
            HttpHeaders newHeaders = userService.addUserIdToCookie(user);

            return ResponseEntity.status(HttpStatus.OK).headers(newHeaders).body(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        try {
            HttpHeaders newHeaders = userService.clearUserCookie();
            logger.info("User logged out successfully.");

            return ResponseEntity.status(HttpStatus.OK).headers(newHeaders).build();
        } catch (Exception e) {
            logger.error("Logout error: {}", e.getMessage());
            return ResponseEntity.status(500).body("An error occurred during logout.");
        }
    }
}
