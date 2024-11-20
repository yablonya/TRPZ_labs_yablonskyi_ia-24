package org.example.mindmappingsoftware.services;

import org.example.mindmappingsoftware.dto.GetMapResponse;
import org.example.mindmappingsoftware.dto.GetMapResponseWithDate;
import org.example.mindmappingsoftware.dto.NodeCreationRequest;
import org.example.mindmappingsoftware.mementos.MindMapMemento;
import org.example.mindmappingsoftware.models.*;
import org.example.mindmappingsoftware.repositories.*;
import org.example.mindmappingsoftware.strategies.NodeProcessingStrategy;
import org.example.mindmappingsoftware.strategies.WithFilesProcessingStrategy;
import org.example.mindmappingsoftware.strategies.WithoutFilesProcessingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MindMapService {
    private final MindMapRepository mindMapRepository;
    private final MindMapHistoryRepository mindMapHistoryRepository;
    private final NodeRepository nodeRepository;
    private final ConnectionRepository connectionRepository;
    private final IconRepository iconRepository;
    private final FileRepository fileRepository;
    private final CommentRepository commentRepository;
    private static final Logger logger = LoggerFactory.getLogger(MindMapService.class);

    @Autowired
    public MindMapService(
            MindMapRepository mindMapRepository,
            MindMapHistoryRepository mindMapHistoryRepository,
            NodeRepository nodeRepository,
            ConnectionRepository connectionRepository,
            IconRepository iconRepository,
            FileRepository fileRepository,
            CommentRepository commentRepository
    ) {
        this.mindMapRepository = mindMapRepository;
        this.mindMapHistoryRepository = mindMapHistoryRepository;
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

    public GetMapResponse getFullMindMap(User user, Long mindMapId) {
        try {
            MindMap mindMap = getMindMap(mindMapId);

            if (!user.getId().equals(mindMap.getCreator().getId())) {
                logger.warn("User {} does not own mind map {}", user.getId(), mindMapId);
                throw new IllegalArgumentException("Mind map does not belong to the user.");
            }

            List<Node> nodes = nodeRepository.findAllByMindMap(mindMap);
            GetMapResponse fullMindMap = new GetMapResponse();
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

    public void addNode(NodeCreationRequest node) {
        try {
            MindMap mindMap = getMindMap(Long.parseLong(node.getMindMapId()));

            Node newNode = new Node();
            newNode.setMindMap(mindMap);
            newNode.setType(node.getType());
            newNode.setContent(node.getContent());
            newNode.setXPosition(node.getXPosition());
            newNode.setYPosition(node.getYPosition());

            NodeProcessingStrategy strategy;
            if (node.getNodeFiles() != null) {
                logger.info("Processing node with files for mind map {}", mindMap.getId());
                strategy = new WithFilesProcessingStrategy(nodeRepository, fileRepository);
                strategy.process(newNode, node.getNodeFiles());
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

    public void saveMindMapState(Long mindMapId) {
        try {
            GetMapResponse fullMindMap = getFullMindMap(getMindMap(mindMapId).getCreator(), mindMapId);
            MindMapMemento memento = fullMindMap.saveState();

            MindMapHistory history = new MindMapHistory();
            history.setMindMap(fullMindMap.getMindMap());
            history.setSnapshot(memento.getSnapshot());
            history.setSavedAt(LocalDateTime.now());

            mindMapHistoryRepository.save(history);

            logger.info("Saved state for mind map with ID {}", mindMapId);
        } catch (NoSuchElementException e) {
            logger.warn("Failed to save state: Mind map with ID {} not found", mindMapId);
            throw e;
        } catch (Exception e) {
            logger.error("Error saving state for mind map with ID {}: {}", mindMapId, e.getMessage());
            throw new RuntimeException("Failed to save mind map state", e);
        }
    }

    @Transactional
    public void restoreMindMapState(Long mindMapId, LocalDateTime restoreDate) {
        try {
            MindMapHistory snapshot = mindMapHistoryRepository
                    .findByMindMapIdAndSavedAt(mindMapId, restoreDate);

            if (snapshot == null) {
                logger.warn("No saved state found for mind map with ID {} before {}", mindMapId, restoreDate);
                throw new NoSuchElementException("No saved state found for MindMap with ID: " + mindMapId + " before " + restoreDate);
            }

            MindMapMemento memento = new MindMapMemento(snapshot.getSnapshot());
            GetMapResponse restoredMap = new GetMapResponse();
            restoredMap.restoreState(memento);

            nodeRepository.deleteAllByMindMapId(mindMapId);

            nodeRepository.saveAll(restoredMap.getNodes());

            logger.info("Restored state for mind map with ID {} to snapshot at {}", mindMapId, restoreDate);
        } catch (NoSuchElementException e) {
            logger.warn("Failed to restore state: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error restoring state for mind map with ID {}: {}", mindMapId, e.getMessage());
            throw new RuntimeException("Failed to restore mind map state", e);
        }
    }

    public List<GetMapResponseWithDate> getMindMapHistory(Long mindMapId) {
        try {
            List<MindMapHistory> history = mindMapHistoryRepository.findAllByMindMapIdOrderBySavedAtDesc(mindMapId);

            if (history.isEmpty()) {
                logger.warn("No history found for mind map with ID {}", mindMapId);
                return Collections.emptyList();
            }

            List<GetMapResponseWithDate> responseHistory = history.stream()
                    .map(entry -> {
                        try {
                            MindMapMemento memento = new MindMapMemento(entry.getSnapshot());
                            GetMapResponse response = new GetMapResponse();
                            response.restoreState(memento);
                            return new GetMapResponseWithDate(response, entry.getSavedAt());
                        } catch (Exception e) {
                            logger.error("Error deserializing snapshot for mind map with ID {}: {}", mindMapId, e.getMessage());
                            throw new RuntimeException("Error deserializing snapshot", e);
                        }
                    })
                    .collect(Collectors.toList());

            logger.info("Retrieved history for mind map with ID {}", mindMapId);
            return responseHistory;
        } catch (Exception e) {
            logger.error("Error retrieving history for mind map with ID {}: {}", mindMapId, e.getMessage());
            throw new RuntimeException("Failed to retrieve mind map history", e);
        }
    }
}


