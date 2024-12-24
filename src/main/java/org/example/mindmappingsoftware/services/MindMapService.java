package org.example.mindmappingsoftware.services;

import org.example.mindmappingsoftware.dto.FullMindMap;
import org.example.mindmappingsoftware.dto.NodeCreationRequest;
import org.example.mindmappingsoftware.dto.NodeFile;
import org.example.mindmappingsoftware.dto.NodeIcon;
import org.example.mindmappingsoftware.models.*;
import org.example.mindmappingsoftware.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MindMapService {
    private final MindMapRepository mindMapRepository;
    private final NodeRepository nodeRepository;
    private final ConnectionRepository connectionRepository;
    private final IconRepository iconRepository;
    private final FileRepository fileRepository;
    private static final Logger logger = LoggerFactory.getLogger(MindMapService.class);

    @Autowired
    public MindMapService(
            MindMapRepository mindMapRepository,
            NodeRepository nodeRepository,
            ConnectionRepository connectionRepository,
            IconRepository iconRepository,
            FileRepository fileRepository
    ) {
        this.mindMapRepository = mindMapRepository;
        this.nodeRepository = nodeRepository;
        this.connectionRepository = connectionRepository;
        this.iconRepository = iconRepository;
        this.fileRepository = fileRepository;
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

    public void updateMindMapName(User user, String mindMapId, String newName) {
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

    public MindMap getMindMap(String mindMapId) {
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

    @Transactional
    public void deleteMindMap(User user, String mindMapId) {
        try {
            FullMindMap fullMindMap = getFullMindMap(user, mindMapId);

            for (Node node : fullMindMap.getNodes()) {
                fileRepository.deleteByNode(node);
                iconRepository.deleteByNode(node);
            }

            connectionRepository.deleteAllByMindMap(fullMindMap.getMindMap());
            connectionRepository.flush();
            nodeRepository.deleteAllByMindMap(fullMindMap.getMindMap());
            nodeRepository.flush();
            mindMapRepository.deleteById(fullMindMap.getMindMap().getId());

            logger.info("Mind map deleted successfully for user with id: {}", user.getId());
        } catch (Exception e) {
            logger.error("Error deleting mind map for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Failed to delete mind map", e);
        }
    }

    public void restoreMindMap(FullMindMap fullMindMap) {
        try {
            mindMapRepository.save(fullMindMap.getMindMap());
            nodeRepository.saveAll(fullMindMap.getNodes());
            connectionRepository.saveAll(fullMindMap.getConnections());
            iconRepository.saveAll(fullMindMap.getIcons());
            fileRepository.saveAll(fullMindMap.getFiles());

            logger.info("Mind map restored successfully");
        } catch (Exception e) {
            logger.error("Error restoring mind map {}: {}", fullMindMap.getMindMap().getId(), e.getMessage());
            throw new RuntimeException("Failed to restoring mind map", e);
        }
    }

    public List<Node> getNodesByMindMapId(User user, String mindMapId) {
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

    public FullMindMap getFullMindMap(User user, String mindMapId) {
        try {
            MindMap mindMap = getMindMap(mindMapId);

            if (!user.getId().equals(mindMap.getCreator().getId())) {
                logger.warn("User {} does not own mind map {}", user.getId(), mindMapId);
                throw new IllegalArgumentException("Mind map does not belong to the user.");
            }

            FullMindMap fullMindMap = new FullMindMap();
            List<Node> nodes = nodeRepository.findAllByMindMap(mindMap);
            List<Connection> connections = connectionRepository.findAllByMindMap(mindMap);
            List<Icon> icons = nodes.stream()
                    .flatMap(node -> iconRepository.findAllByNode(node).stream())
                    .collect(Collectors.toList());

            List<File> files = nodes.stream()
                    .flatMap(node -> fileRepository.findAllByNode(node).stream())
                    .collect(Collectors.toList());

            fullMindMap.setMindMap(mindMap);
            fullMindMap.setNodes(nodes);
            fullMindMap.setConnections(connections);
            fullMindMap.setIcons(icons);
            fullMindMap.setFiles(files);

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
            MindMap mindMap = getMindMap(nodeRequest.getMindMapId());

            Node newNode = new Node();
            newNode.setMindMap(mindMap);
            newNode.setContent(nodeRequest.getContent());
            newNode.setXPosition(nodeRequest.getxPosition());
            newNode.setYPosition(nodeRequest.getyPosition());

            nodeRepository.save(newNode);

            List<Node> existingNodes = nodeRepository.findAllByMindMap(mindMap)
                    .stream()
                    .filter(node -> !node.getId().equals(newNode.getId()))
                    .toList();

            if (!existingNodes.isEmpty()) {
                Node nearestNode = existingNodes.stream()
                        .min(Comparator.comparingDouble(node -> Math.sqrt(
                                Math.pow(node.getXPosition() - newNode.getXPosition(), 2) +
                                        Math.pow(node.getYPosition() - newNode.getYPosition(), 2))))
                        .orElse(null);

                Connection connection = new Connection();
                connection.setMindMap(newNode.getMindMap());
                connection.setFromNode(newNode);
                connection.setToNode(nearestNode);
                connectionRepository.save(connection);
            }

            if (nodeRequest.getNodeIcons() != null && !nodeRequest.getNodeIcons().isEmpty()) {
                List<Icon> iconEntities = new ArrayList<>();

                for (NodeIcon nodeIcon : nodeRequest.getNodeIcons()) {
                    Icon icon = new Icon();
                    icon.setType(nodeIcon.getType());
                    icon.setContent(nodeIcon.getContent());
                    icon.setNode(newNode);
                    iconEntities.add(icon);
                }

                iconRepository.saveAll(iconEntities);
            }

            if (nodeRequest.getNodeFiles() != null && !nodeRequest.getNodeFiles().isEmpty()) {
                List<File> fileEntities = new ArrayList<>();

                for (NodeFile nodeFile : nodeRequest.getNodeFiles()) {
                    File file = new File();
                    file.setUrl(nodeFile.getUrl());
                    file.setType(nodeFile.getType());
                    file.setNode(newNode);
                    fileEntities.add(file);
                }

                fileRepository.saveAll(fileEntities);
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

    public void updateNodes(User user, String mindMapId, List<Node> updatedNodes) {
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

    public List<Connection> getConnectionsByMindMapId(User user, String mindMapId) {
        try {
            MindMap mindMap = getMindMap(mindMapId);

            if (!user.getId().equals(mindMap.getCreator().getId())) {
                throw new IllegalArgumentException("Mind map does not belong to the user.");
            }

            List<Node> nodes = nodeRepository.findAllByMindMap(mindMap);

            if (nodes.isEmpty()) {
                return new ArrayList<>();
            }

            return connectionRepository.findAll().stream()
                    .filter(connection -> nodes.contains(connection.getFromNode()) || nodes.contains(connection.getToNode()))
                    .collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            logger.warn("Mind map with ID {} not found", mindMapId);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving connections for mind map {}: {}", mindMapId, e.getMessage());
            throw new RuntimeException("Failed to retrieve connections", e);
        }
    }

    public List<File> getFilesByNodeId(User user, String nodeId) {
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

    public List<Icon> getIconsByNodeId(User user, String nodeId) {
        try {
            Node node = nodeRepository.findById(nodeId)
                    .orElseThrow(() -> new NoSuchElementException("Node not found: " + nodeId));

            MindMap mindMap = node.getMindMap();

            if (!user.getId().equals(mindMap.getCreator().getId())) {
                throw new IllegalArgumentException("Node does not belong to a mind map owned by the user.");
            }

            return iconRepository.findAllByNode(node);
        } catch (NoSuchElementException e) {
            logger.warn("Error retrieving icons for node {}: {}", nodeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving icons for node {}: {}", nodeId, e.getMessage());
            throw new RuntimeException("Failed to retrieve icons for the node", e);
        }
    }

    public void removeIcon(User user, String nodeId, String iconId) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new NoSuchElementException("Node not found: " + nodeId));
        MindMap mindMap = node.getMindMap();

        if (!user.getId().equals(mindMap.getCreator().getId())) {
            throw new IllegalArgumentException("Node does not belong to a mind map owned by the user.");
        }

        Icon icon = iconRepository.findById(iconId)
                .orElseThrow(() -> new NoSuchElementException("Icon not found: " + iconId));

        if (!icon.getNode().getId().equals(nodeId)) {
            throw new IllegalArgumentException("Icon does not belong to the specified node.");
        }

        iconRepository.delete(icon);
        logger.info("Icon {} removed from node {} by user {}", iconId, nodeId, user.getId());
    }

    public void removeFile(User user, String nodeId, String fileId) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new NoSuchElementException("Node not found: " + nodeId));
        MindMap mindMap = node.getMindMap();

        if (!user.getId().equals(mindMap.getCreator().getId())) {
            throw new IllegalArgumentException("Node does not belong to a mind map owned by the user.");
        }

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new NoSuchElementException("File not found: " + fileId));

        if (!file.getNode().getId().equals(nodeId)) {
            throw new IllegalArgumentException("File does not belong to the specified node.");
        }

        fileRepository.delete(file);
        logger.info("File {} removed from node {} by user {}", fileId, nodeId, user.getId());
    }

    public void addIcon(User user, String nodeId, NodeIcon icon) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new NoSuchElementException("Node not found: " + nodeId));
        MindMap mindMap = node.getMindMap();

        if (!user.getId().equals(mindMap.getCreator().getId())) {
            throw new IllegalArgumentException("Node does not belong to a mind map owned by the user.");
        }

        Icon newIcon = new Icon();
        newIcon.setType(icon.getType());
        newIcon.setContent(icon.getContent());
        newIcon.setNode(node);

        iconRepository.save(newIcon);
        logger.info("Icon added to node {} by user {}", nodeId, user.getId());
    }

    public void addNodeFile(User user, String nodeId, NodeFile file) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new NoSuchElementException("Node not found: " + nodeId));
        MindMap mindMap = node.getMindMap();

        if (!user.getId().equals(mindMap.getCreator().getId())) {
            throw new IllegalArgumentException("Node does not belong to a mind map owned by the user.");
        }

        File newFile = new File();
        newFile.setUrl(file.getUrl());
        newFile.setType(file.getType());
        newFile.setNode(node);

        newFile.setNode(node);
        fileRepository.save(newFile);
        logger.info("File added to node {} by user {}", nodeId, user.getId());
    }

    public void deleteNode(String nodeId) {
        try {
            Node node = nodeRepository.findById(nodeId)
                    .orElseThrow(() -> new NoSuchElementException("Node not found: " + nodeId));

            connectionRepository.deleteAll(connectionRepository.findByFromNodeOrToNode(node, node));

            iconRepository.deleteAll(iconRepository.findAllByNode(node));
            fileRepository.deleteAll(fileRepository.findAllByNode(node));
            nodeRepository.delete(node);
            logger.info("Node {} deleted successfully", nodeId);
        } catch (NoSuchElementException e) {
            logger.warn("Error deleting node {}: {}", nodeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting node {}: {}", nodeId, e.getMessage());
            throw new RuntimeException("Failed to delete node", e);
        }
    }

    public void addConnection(String fromNodeId, String toNodeId) {
        try {
            Node fromNode = nodeRepository.findById(fromNodeId)
                    .orElseThrow(() -> new NoSuchElementException("From Node not found: " + fromNodeId));
            Node toNode = nodeRepository.findById(toNodeId)
                    .orElseThrow(() -> new NoSuchElementException("To Node not found: " + toNodeId));

            if (connectionRepository.existsByFromNodeAndToNode(fromNode, toNode)) {
                throw new IllegalArgumentException("Connection already exists between the specified nodes.");
            }

            Connection connection = new Connection();
            connection.setMindMap(fromNode.getMindMap());
            connection.setFromNode(fromNode);
            connection.setToNode(toNode);
            connectionRepository.save(connection);

            logger.info("Connection added successfully between nodes {} and {}", fromNodeId, toNodeId);
        } catch (NoSuchElementException e) {
            logger.warn("Error adding connection: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid connection request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error adding connection between nodes {} and {}: {}", fromNodeId, toNodeId, e.getMessage());
            throw new RuntimeException("Failed to add connection", e);
        }
    }

    public void deleteConnection(String connectionId) {
        try {
            Connection connection = connectionRepository.findById(connectionId)
                    .orElseThrow(() -> new NoSuchElementException("Connection not found: " + connectionId));

            connectionRepository.delete(connection);
            logger.info("Connection {} deleted successfully", connectionId);
        } catch (NoSuchElementException e) {
            logger.warn("Error deleting connection {}: {}", connectionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting connection {}: {}", connectionId, e.getMessage());
            throw new RuntimeException("Failed to delete connection", e);
        }
    }
}
