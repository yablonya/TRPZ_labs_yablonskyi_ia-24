package org.example.mindmappingsoftware.services;

import org.example.mindmappingsoftware.dto.FullMindMap;
import org.example.mindmappingsoftware.dto.NodeCreationRequest;
import org.example.mindmappingsoftware.models.*;
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
        try {
            if (mapName == null || mapName.isEmpty()) {
                throw new IllegalArgumentException("Map name cannot be null or empty.");
            }

            MindMap newMindMap = new MindMap();
            newMindMap.setName(mapName);
            newMindMap.setCreator(user);

            mindMapRepository.save(newMindMap);

            logger.info("Mind map created successfully: {}", newMindMap);
            return newMindMap;
        } catch (Exception e) {
            logger.error("Error creating mind map for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Failed to create mind map", e);
        }
    }

    public void updateMindMapName(User user, Long mindMapId, String newName) {
        try {
            if (newName == null || newName.isEmpty()) {
                throw new IllegalArgumentException("New name cannot be null or empty.");
            }

            MindMap mindMap = getMindMap(mindMapId);

            if (!user.getId().equals(mindMap.getCreator().getId())) {
                logger.warn("User {} does not own mind map {}", user.getId(), mindMapId);
                throw new IllegalArgumentException("Mind map does not belong to the user.");
            }

            mindMap.setName(newName);
            mindMapRepository.save(mindMap);

            logger.info("Mind map name updated successfully for mind map {} by user {}", mindMapId, user.getId());
        } catch (NoSuchElementException e) {
            logger.warn("Mind map with ID {} not found for user {}", mindMapId, user.getId());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating mind map name for mind map {} by user {}: {}", mindMapId, user.getId(), e.getMessage());
            throw new RuntimeException("Failed to update mind map name", e);
        }
    }

    public void updateNodes(User user, Long mindMapId, List<Node> updatedNodes) {
        try {
            MindMap mindMap = getMindMap(mindMapId);

            if (!user.getId().equals(mindMap.getCreator().getId())) {
                logger.warn("User {} does not own mind map {}", user.getId(), mindMapId);
                throw new IllegalArgumentException("Mind map does not belong to the user.");
            }

            for (Node updatedNode : updatedNodes) {
                Node node = nodeRepository.findById(updatedNode.getId())
                        .orElseThrow(() -> new NoSuchElementException("Node not found: " + updatedNode.getId()));

                if (!node.getMindMap().getId().equals(mindMapId)) {
                    logger.warn("Node {} does not belong to mind map {}", node.getId(), mindMapId);
                    throw new IllegalArgumentException("Node does not belong to the specified mind map.");
                }

                node.setContent(updatedNode.getContent());
                node.setXPosition(updatedNode.getXPosition());
                node.setYPosition(updatedNode.getYPosition());

                nodeRepository.save(node);
                logger.info("Node {} updated successfully in mind map {}", node.getId(), mindMapId);
            }
        } catch (NoSuchElementException e) {
            logger.warn("Error updating updatedNodes for mind map {}: {}", mindMapId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating updatedNodes for mind map {}: {}", mindMapId, e.getMessage());
            throw new RuntimeException("Failed to update updatedNodes", e);
        }
    }


    public MindMap getMindMap(Long mindMapId) {
        try {
            return mindMapRepository.findById(mindMapId)
                    .orElseThrow(() -> new NoSuchElementException("Mind map not found."));
        } catch (NoSuchElementException e) {
            logger.warn("Mind map with ID {} not found", mindMapId);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving mind map with ID {}: {}", mindMapId, e.getMessage());
            throw new RuntimeException("Failed to retrieve mind map", e);
        }
    }

    public List<Node> getNodesByMindMapId(User user, Long mindMapId) {
        try {
            MindMap mindMap = getMindMap(mindMapId);

            if (!user.getId().equals(mindMap.getCreator().getId())) {
                throw new IllegalArgumentException("Mind map does not belong to the user.");
            }

            return nodeRepository.findAllByMindMap(mindMap);
        } catch (Exception e) {
            logger.error("Error retrieving nodes for mind map {}: {}", mindMapId, e.getMessage());
            throw new RuntimeException("Failed to retrieve nodes", e);
        }
    }

    public List<MindMap> getAllMindMaps(User user) {
        try {
            return mindMapRepository.findByCreator(user);
        } catch (NoSuchElementException e) {
            logger.warn("Mind maps for user with ID {} not found", user.getId());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving mind maps for user with ID {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Failed to retrieve mind map", e);
        }
    }

    public FullMindMap getFullMindMap(User user, Long mindMapId) {
        try {
            MindMap mindMap = getMindMap(mindMapId);

            if (!user.getId().equals(mindMap.getCreator().getId())) {
                logger.warn("User {} does not own mind map {}", user.getId(), mindMapId);
                throw new IllegalArgumentException("Mind map does not belong to the user.");
            }

            List<Node> nodes = nodeRepository.findAllByMindMap(mindMap);
            FullMindMap fullMindMap = new FullMindMap();
            fullMindMap.setMindMap(mindMap);
            fullMindMap.setNodes(nodes);

            logger.info("Retrieved full mind map for user {}: {}", user.getId(), mindMapId);
            return fullMindMap;
        } catch (NoSuchElementException e) {
            logger.warn("Mind map with ID {} not found for user {}", mindMapId, user.getId());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving full mind map for user {} and mind map ID {}: {}", user.getId(), mindMapId, e.getMessage());
            throw new RuntimeException("Failed to retrieve full mind map", e);
        }
    }

    public void addNode(NodeCreationRequest nodeRequest) {
        try {
            MindMap mindMap = getMindMap(Long.parseLong(nodeRequest.getMindMapId()));

            Node newNode = new Node();
            newNode.setMindMap(mindMap);
            newNode.setContent(nodeRequest.getContent());
            newNode.setXPosition(nodeRequest.getxPosition());
            newNode.setYPosition(nodeRequest.getyPosition());

            NodeProcessingStrategy strategy;
            if (nodeRequest.getNodeFiles() != null && !nodeRequest.getNodeFiles().isEmpty()) {
                logger.info("Processing node with files for mind map {}", mindMap.getId());
                strategy = new WithFilesProcessingStrategy(nodeRepository, fileRepository);

                strategy.process(newNode, nodeRequest.getNodeFiles());
            } else {
                logger.info("Processing node without files for mind map {}", mindMap.getId());
                strategy = new WithoutFilesProcessingStrategy(nodeRepository);
                strategy.process(newNode);
            }

            logger.info("Node added successfully to mind map {}", mindMap.getId());
        } catch (NoSuchElementException e) {
            logger.warn("Mind map not found for node addition: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error adding node: {}", e.getMessage());
            throw new RuntimeException("Failed to add node", e);
        }
    }

    public List<File> getFilesByNodeId(User user, Long nodeId) {
        try {
            Node node = nodeRepository.findById(nodeId)
                    .orElseThrow(() -> new NoSuchElementException("Node not found: " + nodeId));

            MindMap mindMap = node.getMindMap();

            if (!user.getId().equals(mindMap.getCreator().getId())) {
                throw new IllegalArgumentException("Node does not belong to a mind map owned by the user.");
            }

            return fileRepository.findAllByNode(node);
        } catch (NoSuchElementException e) {
            logger.warn("Error retrieving files for node {}: {}", nodeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving files for node {}: {}", nodeId, e.getMessage());
            throw new RuntimeException("Failed to retrieve files for the node", e);
        }
    }
}


