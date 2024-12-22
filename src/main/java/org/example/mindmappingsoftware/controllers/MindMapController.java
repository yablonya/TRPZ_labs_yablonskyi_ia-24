package org.example.mindmappingsoftware.controllers;

import org.example.mindmappingsoftware.dto.FullMindMap;
import org.example.mindmappingsoftware.dto.NodeCreationRequest;
import org.example.mindmappingsoftware.dto.NodeFile;
import org.example.mindmappingsoftware.dto.NodeIcon;
import org.example.mindmappingsoftware.models.*;
import org.example.mindmappingsoftware.services.MindMapHistoryService;
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
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MindMapController {
    private final MindMapService mindMapService;
    private final MindMapHistoryService mindMapHistoryService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(MindMapController.class);

    @Autowired
    public MindMapController(
            MindMapService mindMapService,
            MindMapHistoryService mindMapHistoryService,
            UserService userService
    ) {
        this.mindMapService = mindMapService;
        this.mindMapHistoryService = mindMapHistoryService;
        this.userService = userService;
    }

    private User validateUser(String userId) {
        if (userId.equals("null")) {
            throw new IllegalStateException("User not logged in.");
        } try {
            return userService.getUser(userId);
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllMindMaps(
            @CookieValue(value = "userId", required = false) String userId
    ) {
        try {
            User user = validateUser(userId);

            List<MindMap> mindMaps = mindMapService.getAllMindMaps(user);

            return ResponseEntity.status(HttpStatus.OK).body(mindMaps);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mind maps not found.");
        } catch (Exception e) {
            logger.error("Error fetching mind maps", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/{mindMapId}")
    public ResponseEntity<?> getMindMap(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String mindMapId
    ) {
        try {
            User user = validateUser(userId);

            FullMindMap mindMap = mindMapService.getFullMindMap(user, mindMapId);

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

    @DeleteMapping("/{mindMapId}/delete")
    public ResponseEntity<?> deleteMindMap(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String mindMapId
    ) {
        try {
            User user = validateUser(userId);

            mindMapHistoryService.deleteMindMapHistory(user, mindMapId);
            mindMapService.deleteMindMap(user, mindMapId);

            return ResponseEntity.status(HttpStatus.CREATED).body("Mind map deleted successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating mind map", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<?> saveMindMapState(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String id
    ) {
        try {
            validateUser(userId);

            mindMapHistoryService.saveMindMapState(id);

            return ResponseEntity.ok("State saved successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        } catch (Exception e) {
            logger.error("Error saving mind map state", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving state");
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getMindMapHistory(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String id
    ) {
        try {
            validateUser(userId);

            List<FullMindMap> history = mindMapHistoryService.getMindMapHistory(id);

            return ResponseEntity.ok(history);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        } catch (Exception e) {
            logger.error("Error fetching mind map history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching history");
        }
    }

    @PostMapping("/{mindMapId}/restore")
    public ResponseEntity<?> restoreMindMapState(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String mindMapId,
            @RequestParam("restoreDate") String restoreDate
    ) {
        try {
            User user = validateUser(userId);

            LocalDateTime parsedDate = LocalDateTime.parse(restoreDate);
            logger.info("{}", parsedDate);
            mindMapHistoryService.restoreMindMapState(user, mindMapId, parsedDate);

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

    @PutMapping("/{mindMapId}/update-name")
    public ResponseEntity<?> updateMindMapName(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String mindMapId,
            @RequestParam String newName
    ) {
        try {
            User user = validateUser(userId);
            mindMapService.updateMindMapName(user, mindMapId, newName);

            return ResponseEntity.status(HttpStatus.OK).body("Mind map name updated successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating mind map name", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/{mindMapId}/nodes")
    public ResponseEntity<?> getNodes(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String mindMapId
    ) {
        try {
            User user = validateUser(userId);
            List<Node> nodes = mindMapService.getNodesByMindMapId(user, mindMapId);
            return ResponseEntity.ok(nodes);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching nodes for mind map {}: {}", mindMapId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PutMapping("/{mindMapId}/update-nodes")
    public ResponseEntity<?> updateNodes(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String mindMapId,
            @RequestBody List<Node> nodes
    ) {
        try {
            User user = validateUser(userId);
            mindMapService.updateNodes(user, mindMapId, nodes);

            return ResponseEntity.status(HttpStatus.OK).body("Nodes updated successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating nodes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/add-connection")
    public ResponseEntity<?> addConnection(
            @CookieValue(value = "userId", required = false) String userId,
            @RequestParam String fromNodeId,
            @RequestParam String toNodeId
    ) {
        try {
            validateUser(userId);
            mindMapService.addConnection(fromNodeId, toNodeId);
            return ResponseEntity.ok("Connection added successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nodes not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding connection between nodes {} and {}: {}", fromNodeId, toNodeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add connection.");
        }
    }

    @GetMapping("/{mindMapId}/connections")
    public ResponseEntity<?> getConnectionsByMindMapId(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String mindMapId
    ) {
        try {
            User user = validateUser(userId);
            List<Connection> connections = mindMapService.getConnectionsByMindMapId(user, mindMapId);
            return ResponseEntity.ok(connections);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mind map or connections not found.");
        } catch (Exception e) {
            logger.error("Error fetching connections for mind map {}: {}", mindMapId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @DeleteMapping("/delete-connection")
    public ResponseEntity<?> deleteConnection(
            @CookieValue(value = "userId", required = false) String userId,
            @RequestParam String connectionId
    ) {
        try {
            validateUser(userId);
            mindMapService.deleteConnection(connectionId);
            return ResponseEntity.ok("Connection deleted successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Connection not found.");
        } catch (Exception e) {
            logger.error("Error deleting connection {}: {}", connectionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete connection.");
        }
    }

    @PostMapping("/node/add")
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

    @DeleteMapping("/node/delete")
    public ResponseEntity<?> deleteNode(
            @CookieValue(value = "userId", required = false) String userId,
            @RequestParam String nodeId
    ) {
        try {
            validateUser(userId);
            mindMapService.deleteNode(nodeId);
            return ResponseEntity.ok("Node deleted successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Node not found.");
        } catch (Exception e) {
            logger.error("Error deleting node {}: {}", nodeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete node.");
        }
    }

    @PostMapping("/node/{nodeId}/add-icon")
    public ResponseEntity<?> addIcon(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String nodeId,
            @RequestBody NodeIcon icon
    ) {
        try {
            User user = validateUser(userId);
            mindMapService.addIcon(user, nodeId, icon);
            return ResponseEntity.ok("Icon added successfully.");
        } catch (Exception e) {
            logger.error("Error adding icon for node {}: {}", nodeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add icon.");
        }
    }

    @GetMapping("/node/{nodeId}/icons")
    public ResponseEntity<?> getNodeIcons(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String nodeId
    ) {
        try {
            User user = validateUser(userId);
            List<Icon> icons = mindMapService.getIconsByNodeId(user, nodeId);
            return ResponseEntity.ok(icons);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error retrieving icons for node {}: {}", nodeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @DeleteMapping("/node/{nodeId}/delete-icon")
    public ResponseEntity<?> deleteIcon(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String nodeId,
            @RequestParam String iconId
    ) {
        try {
            User user = validateUser(userId);
            mindMapService.removeIcon(user, nodeId, iconId);
            return ResponseEntity.ok("Icon removed successfully.");
        } catch (Exception e) {
            logger.error("Error removing icon for node {}: {}", nodeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove icon.");
        }
    }

    @PostMapping("/node/{nodeId}/add-file")
    public ResponseEntity<?> addNodeFile(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String nodeId,
            @RequestBody NodeFile newFile
    ) {
        try {
            User user = validateUser(userId);
            mindMapService.addNodeFile(user, nodeId, newFile);
            return ResponseEntity.ok("File added successfully.");
        } catch (Exception e) {
            logger.error("Error adding file for node {}: {}", nodeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add file.");
        }
    }

    @GetMapping("/node/{nodeId}/files")
    public ResponseEntity<?> getNodeFiles(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String nodeId
    ) {
        try {
            User user = validateUser(userId);
            List<File> files = mindMapService.getFilesByNodeId(user, nodeId);
            return ResponseEntity.ok(files);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error retrieving files for node {}: {}", nodeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @DeleteMapping("/node/{nodeId}/delete-file")
    public ResponseEntity<?> deleteFile(
            @CookieValue(value = "userId", required = false) String userId,
            @PathVariable String nodeId,
            @RequestParam String fileId
    ) {
        try {
            User user = validateUser(userId);
            mindMapService.removeFile(user, nodeId, fileId);
            return ResponseEntity.ok("File removed successfully.");
        } catch (Exception e) {
            logger.error("Error removing file for node {}: {}", nodeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove file.");
        }
    }
}

