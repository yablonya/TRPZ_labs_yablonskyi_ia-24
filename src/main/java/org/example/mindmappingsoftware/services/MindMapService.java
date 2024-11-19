package org.example.mindmappingsoftware.services;

import org.example.mindmappingsoftware.dto.GetMapResponse;
import org.example.mindmappingsoftware.dto.NodeCreationRequest;
import org.example.mindmappingsoftware.models.File;
import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.Node;
import org.example.mindmappingsoftware.models.User;
import org.example.mindmappingsoftware.repositories.*;
import org.example.mindmappingsoftware.strategies.NodeProcessingStrategy;
import org.example.mindmappingsoftware.strategies.WithFilesProcessingStrategy;
import org.example.mindmappingsoftware.strategies.WithoutFilesProcessingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MindMapService {
    private final MindMapRepository mindMapRepository;
    private final NodeRepository nodeRepository;
    private final ConnectionRepository connectionRepository;
    private final IconRepository iconRepository;
    private final FileRepository fileRepository;
    private final CommentRepository commentRepository;
    private static final Logger logger = LoggerFactory.getLogger(MindMapService.class);

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

    public MindMap createMindMap(User user, String mapName) {
        MindMap newMindMap = new MindMap();
        newMindMap.setName(mapName);
        newMindMap.setCreator(user);

        mindMapRepository.save(newMindMap);

        return newMindMap;
    }

    public MindMap getMindMap(Long mindMapId) {
        return mindMapRepository.findById(mindMapId)
                .orElseThrow(() -> new NoSuchElementException("Mind map not found."));
    }

    public GetMapResponse getFullMindMap(User user, Long mindMapId) {
        MindMap mindMap = mindMapRepository.findById(mindMapId)
                .orElseThrow(() -> new NoSuchElementException("Mind map not found."));

        if (!user.getId().equals(mindMap.getCreator().getId())) {
            throw new IllegalArgumentException("Mind map does not belong to user");
        }

        List<Node> nodes = nodeRepository.findAllByMindMap(mindMap);
        GetMapResponse fullMindMap = new GetMapResponse();
        fullMindMap.setMindMap(mindMap);
        fullMindMap.setNodes(nodes);

        return fullMindMap;
    }

    public void addNode(NodeCreationRequest node) {
        NodeProcessingStrategy strategy;
        MindMap mindMap = getMindMap(Long.parseLong(node.getMindMapId()));

        Node newNode = new Node();
        newNode.setMindMap(mindMap);
        newNode.setType(node.getType());
        newNode.setContent(node.getContent());
        newNode.setXPosition(node.getXPosition());
        newNode.setYPosition(node.getYPosition());

        if (node.getNodeFiles() != null) {
            logger.info("Processing node with files");
            strategy = new WithFilesProcessingStrategy(nodeRepository, fileRepository);
            strategy.process(newNode, node.getNodeFiles());
        } else {
            logger.info("Processing node without files");
            strategy = new WithoutFilesProcessingStrategy(nodeRepository);
            strategy.process(newNode);
        }
    }
}

