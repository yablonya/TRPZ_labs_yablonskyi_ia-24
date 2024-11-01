package org.example.mindmappingsoftware.services;

import org.example.mindmappingsoftware.models.File;
import org.example.mindmappingsoftware.models.Node;
import org.example.mindmappingsoftware.repositories.*;
import org.example.mindmappingsoftware.strategies.NodeProcessingStrategy;
import org.example.mindmappingsoftware.strategies.WithFilesProcessingStrategy;
import org.example.mindmappingsoftware.strategies.WithoutFilesProcessingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MindMapService {
    private final MindMapRepository mindMapRepository;
    private final NodeRepository nodeRepository;
    private final ConnectionRepository connectionRepository;
    private final IconRepository iconRepository;
    private final FileRepository fileRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public MindMapService(
            MindMapRepository mindMapRepository,
            NodeRepository nodeRepository,
            ConnectionRepository connectionRepository,
            IconRepository iconRepository,
            FileRepository fileRepository,
            CommentRepository commentRepository
    ) {
        this.mindMapRepository = mindMapRepository;
        this.nodeRepository = nodeRepository;
        this.connectionRepository = connectionRepository;
        this.iconRepository = iconRepository;
        this.fileRepository = fileRepository;
        this.commentRepository = commentRepository;
    }

    public void addNode(Node node, List<File> nodeFiles) {
        NodeProcessingStrategy strategy;

        if (!nodeFiles.isEmpty()) {
            strategy = new WithFilesProcessingStrategy(nodeRepository, fileRepository);
            strategy.process(node, nodeFiles);
        } else {
            strategy = new WithoutFilesProcessingStrategy(nodeRepository);
            strategy.process(node);
        }
    }
}

