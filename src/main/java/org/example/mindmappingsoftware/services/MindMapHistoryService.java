package org.example.mindmappingsoftware.services;

import org.example.mindmappingsoftware.dto.FullMindMap;
import org.example.mindmappingsoftware.mementos.MindMapMemento;
import org.example.mindmappingsoftware.models.MindMapHistory;
import org.example.mindmappingsoftware.repositories.*;
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
public class MindMapHistoryService {
    private final MindMapHistoryRepository mindMapHistoryRepository;
    private final NodeRepository nodeRepository;
    private final MindMapService mindMapService;
    private static final Logger logger = LoggerFactory.getLogger(MindMapHistoryService.class);

    @Autowired
    public MindMapHistoryService(
            MindMapHistoryRepository mindMapHistoryRepository,
            NodeRepository nodeRepository,
            MindMapService mindMapService
    ) {
        this.mindMapHistoryRepository = mindMapHistoryRepository;
        this.nodeRepository = nodeRepository;
        this.mindMapService = mindMapService;
    }

    public void saveMindMapState(Long mindMapId) {
        try {
            FullMindMap fullMindMap = mindMapService.getFullMindMap(
                    mindMapService.getMindMap(mindMapId).getCreator(),
                    mindMapId
            );
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
            FullMindMap restoredMap = new FullMindMap();
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

    public List<FullMindMap> getMindMapHistory(Long mindMapId) {
        try {
            List<MindMapHistory> history = mindMapHistoryRepository.findAllByMindMapIdOrderBySavedAtDesc(mindMapId);

            if (history.isEmpty()) {
                logger.warn("No history found for mind map with ID {}", mindMapId);
                return Collections.emptyList();
            }

            List<FullMindMap> responseHistory = history.stream()
                    .map(entry -> {
                        try {
                            MindMapMemento memento = new MindMapMemento(entry.getSnapshot());
                            FullMindMap response = new FullMindMap();
                            response.restoreState(memento);
                            response.setSavedAt(entry.getSavedAt());

                            return response;
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
