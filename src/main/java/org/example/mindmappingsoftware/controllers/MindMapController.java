package org.example.mindmappingsoftware.controllers;

import org.example.mindmappingsoftware.dto.GetMapResponse;
import org.example.mindmappingsoftware.dto.NodeCreationRequest;
import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.User;
import org.example.mindmappingsoftware.services.MindMapService;
import org.example.mindmappingsoftware.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/create")
    public ResponseEntity<MindMap> createMindMap(
            @CookieValue(value = "userId", required = false) String userId,
            @RequestParam(value = "name", required = false) String mapName
    ) {
        try {
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (mapName == null || mapName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            User user = userService.getUser(Long.parseLong(userId));
            MindMap savedMindMap = mindMapService.createMindMap(user, mapName);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedMindMap);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{mindMapId}")
    public ResponseEntity<GetMapResponse> getMindMap(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable Long mindMapId
    ) {
        try {
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User user = userService.getUser(Long.parseLong(userId));
            GetMapResponse mindMap = mindMapService.getFullMindMap(user, mindMapId);

            return ResponseEntity.status(HttpStatus.OK).body(mindMap);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add-node")
    public ResponseEntity<Void> addNode(
            @CookieValue(value = "userId", required = false) String userId,
            @RequestBody NodeCreationRequest node
    ) {
        try {
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            mindMapService.addNode(node);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


