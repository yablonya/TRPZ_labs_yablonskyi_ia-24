package org.example.mindmappingsoftware.controllers;

import org.example.mindmappingsoftware.models.File;
import org.example.mindmappingsoftware.models.Node;
import org.example.mindmappingsoftware.services.MindMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mind-map")
public class MindMapController {
    private final MindMapService mindMapService;

    @Autowired
    public MindMapController(MindMapService mindMapService) {
        this.mindMapService = mindMapService;
    }

    @PostMapping("/add-node")
    public ResponseEntity<String> addNode(@RequestBody Node node, @RequestBody List<File> nodeFiles) {
        mindMapService.addNode(node, nodeFiles);
        return ResponseEntity.ok("Nodes processed successfully.");
    }
}


