package org.example.mindmappingsoftware.controllers;

import org.example.mindmappingsoftware.models.User;
import org.example.mindmappingsoftware.services.MindMapHistoryService;
import org.example.mindmappingsoftware.services.MindMapService;
import org.example.mindmappingsoftware.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(
            @PathVariable String userId
    ) {
        try {
            User user = userService.getUser(userId);

            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (Exception e) {
            logger.error("Error fetching user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @CookieValue(value = "userId", required = false) String userIdCookie,
            @RequestBody User updatedUser
    ) {
        try {
            if (userIdCookie == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing user cookie.");
            }

            User user = userService.updateUser(
                    userIdCookie,
                    updatedUser.getName(),
                    updatedUser.getEmail(),
                    updatedUser.getPassword()
            );

            return ResponseEntity.ok(user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (Exception e) {
            logger.error("Error updating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while updating the user.");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(
            @CookieValue(value = "userId", required = false) String userIdCookie
    ) {
        try {
            if (userIdCookie == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing user cookie.");
            }

            userService.deleteUser(userIdCookie);

            HttpHeaders newHeaders = userService.clearUserCookie();
            logger.info("User deleted successfully.");

            return ResponseEntity.status(HttpStatus.OK).headers(newHeaders).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (Exception e) {
            logger.error("Error deleting user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while deleting the user.");
        }
    }
}


