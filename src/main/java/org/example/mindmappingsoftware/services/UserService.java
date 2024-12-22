package org.example.mindmappingsoftware.services;

import jakarta.servlet.http.Cookie;
import org.example.mindmappingsoftware.models.User;
import org.example.mindmappingsoftware.prototypes.CookiePrototype;
import org.example.mindmappingsoftware.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CookiePrototype userCookiePrototype = new CookiePrototype("userId", 24 * 60 * 60);
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User user = createUser(name, email, password);
        User savedProfile = userRepository.save(user);
        logger.info("User profile created: {}", savedProfile.getEmail());

        return savedProfile;
    }

    public User loginUser(String email, String password) {
        logger.info("Attempting to login user with email: {}", email);

        User user = userRepository.findByEmail(email);

        if (user == null || !user.getPassword().equals(password)) {
            logger.warn("Invalid login attempt for email: {}", email);
            throw new IllegalArgumentException("Invalid email or password.");
        }

        logger.info("User logged in successfully: {}", email);
        return user;
    }

    public User getUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            logger.warn("User with ID {} not found", userId);
            throw new NoSuchElementException("There is no user with such ID");
        }

        return user;
    }

    private User createUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        return user;
    }

    public HttpHeaders addUserIdToCookie(User user) {
        Cookie cookie = userCookiePrototype.cloneWithValue(String.valueOf(user.getId()));
        logger.info("User cookie added with ID: {}", user.getId());

        HttpHeaders newHeaders = new HttpHeaders();
        newHeaders.add(
                "Set-Cookie",
                cookie.getName() + "=" + cookie.getValue() + "; Path=/; Max-Age=86400"
        );

        return newHeaders;
    }

    public HttpHeaders clearUserCookie() {
        Cookie cookie = userCookiePrototype.cloneAsCleared();
        logger.info("User cookie cleared.");

        HttpHeaders newHeaders = new HttpHeaders();
        newHeaders.add(
                "Set-Cookie",
                cookie.getName() + "=" + cookie.getValue() + "; Path=/; Max-Age=86400"
        );

        return newHeaders;
    }
}


