package org.example.mindmappingsoftware.controllers;

import org.example.mindmappingsoftware.dto.GetMapResponse;
import org.example.mindmappingsoftware.dto.GetMapResponseWithDate;
import org.example.mindmappingsoftware.dto.NodeCreationRequest;
import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.MindMapHistory;
import org.example.mindmappingsoftware.models.User;
import org.example.mindmappingsoftware.services.MindMapService;
import org.example.mindmappingsoftware.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/mind-map")
public class MindMapController {
    private final MindMapService mindMapService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(MindMapController.class);

    @Autowired
    public MindMapController(MindMapService mindMapService, UserService userService) {
        this.mindMapService = mindMapService;
        this.userService = userService;
    }

    private User validateUser(String userId) {
        if (userId.equals("null")) {
            throw new IllegalStateException("User not logged in.");
        } try {
            return userService.getUser(Long.parseLong(userId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format.");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMindMap(
            @CookieValue(value = "userId", required = false) String userId,
            @RequestParam(value = "name", required = false) String mapName
    ) {
        try {
            User user = validateUser(userId);

            if (mapName == null || mapName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Map name cannot be null or empty.");
            }

            MindMap savedMindMap = mindMapService.createMindMap(user, mapName);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedMindMap);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating mind map", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/{mindMapId}")
    public ResponseEntity<?> getMindMap(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable Long mindMapId
    ) {
        try {
            User user = validateUser(userId);

            GetMapResponse mindMap = mindMapService.getFullMindMap(user, mindMapId);

            return ResponseEntity.status(HttpStatus.OK).body(mindMap);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mind map not found.");
        } catch (Exception e) {
            logger.error("Error fetching mind map", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/add-node")
    public ResponseEntity<?> addNode(
            @CookieValue(value = "userId", required = false) String userId,
            @RequestBody NodeCreationRequest node
    ) {
        try {
            validateUser(userId);

            mindMapService.addNode(node);

            return ResponseEntity.status(HttpStatus.OK).body("Node added successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mind map or related resource not found.");
        } catch (Exception e) {
            logger.error("Error adding node", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<?> saveMindMapState(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable Long id
    ) {
        try {
            validateUser(userId);

            mindMapService.saveMindMapState(id);

            return ResponseEntity.ok("State saved successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        } catch (Exception e) {
            logger.error("Error saving mind map state", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving state");
        }
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreMindMapState(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable Long id,
            @RequestParam("restoreDate") String restoreDate
    ) {
        try {
            validateUser(userId);

            LocalDateTime parsedDate = LocalDateTime.parse(restoreDate);
            logger.info("{}", parsedDate);
            mindMapService.restoreMindMapState(id, parsedDate);

            return ResponseEntity.ok("State restored successfully to snapshot at " + restoreDate);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format. Use ISO-8601 format (e.g., 2024-11-19T15:30:00).");
        } catch (Exception e) {
            logger.error("Error restoring mind map state", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error restoring state");
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getMindMapHistory(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable Long id
    ) {
        try {
            validateUser(userId);

            List<GetMapResponseWithDate> history = mindMapService.getMindMapHistory(id);

            return ResponseEntity.ok(history);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        } catch (Exception e) {
            logger.error("Error fetching mind map history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching history");
        }
    }
}



