package org.example.mindmappingsoftware.services;

import org.example.mindmappingsoftware.dto.FullMindMap;
import org.example.mindmappingsoftware.mementos.MindMapMemento;
import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.MindMapSnapshot;
import org.example.mindmappingsoftware.models.User;
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
    private final MindMapService mindMapService;
    private static final Logger logger = LoggerFactory.getLogger(MindMapHistoryService.class);

    @Autowired
    public MindMapHistoryService(
            MindMapHistoryRepository mindMapHistoryRepository,
            MindMapService mindMapService
    ) {
        this.mindMapHistoryRepository = mindMapHistoryRepository;
        this.mindMapService = mindMapService;
    }

    public void saveMindMapState(String mindMapId) {
        try {
            FullMindMap fullMindMap = mindMapService.getFullMindMap(
                    mindMapService.getMindMap(mindMapId).getCreator(),
                    mindMapId
            );

            MindMapSnapshot history = new MindMapSnapshot();
            history.setSavedAt(LocalDateTime.now());
            history.setMindMap(fullMindMap.getMindMap());
            fullMindMap.setSnapshotId(history.getId());
            MindMapMemento memento = fullMindMap.saveState();
            history.setSnapshot(memento.snapshot());

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
    public void restoreMindMapState(User user, String mindMapId, LocalDateTime restoreDate) {
        try {
            List<MindMapSnapshot> allHistory = mindMapHistoryRepository.findAllByMindMapId(mindMapId);
            MindMapSnapshot snapshot = mindMapHistoryRepository
                    .findByMindMapIdAndSavedAt(mindMapId, restoreDate);

            if (snapshot == null) {
                logger.warn("No saved state found for mind map with ID {} before {}", mindMapId, restoreDate);
                throw new NoSuchElementException("No saved state found for MindMap with ID: " + mindMapId + " before " + restoreDate);
            }

            MindMapMemento memento = new MindMapMemento(snapshot.getSnapshot());
            FullMindMap restoredMap = new FullMindMap();
            restoredMap.restoreState(memento);
            mindMapHistoryRepository.deleteAllByMindMapId(mindMapId);
            mindMapHistoryRepository.flush();
            mindMapService.deleteMindMap(user, mindMapId);
            mindMapService.restoreMindMap(restoredMap);
            mindMapHistoryRepository.saveAll(allHistory);

            logger.info("Restored state for mind map with ID {} to snapshot at {}", mindMapId, restoreDate);
        } catch (NoSuchElementException e) {
            logger.warn("Failed to restore state: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error restoring state for mind map with ID {}: {}", mindMapId, e.getMessage());
            throw new RuntimeException("Failed to restore mind map state", e);
        }
    }

    public List<FullMindMap> getMindMapHistory(String mindMapId) {
        try {
            List<MindMapSnapshot> history = mindMapHistoryRepository.findAllByMindMapIdOrderBySavedAtDesc(mindMapId);

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

    public void deleteMindMapSnapshot(User user, String mindMapId, String snapshot) {
        try {
            MindMap mindMap = mindMapService.getMindMap(mindMapId);
            mindMapHistoryRepository.findById(snapshot);
            mindMapHistoryRepository.deleteAllByMindMapId(mindMapId);

            logger.info("Mind map deleted successfully for user with id: {}", user.getId());
        } catch (Exception e) {
            logger.error("Error deleting mind map for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Failed to delete mind map", e);
        }
    }

    public void deleteMindMapHistory(User user, String mindMapId) {
        try {
            mindMapHistoryRepository.deleteAllByMindMapId(mindMapId);

            logger.info("Mind map deleted successfully for user with id: {}", user.getId());
        } catch (Exception e) {
            logger.error("Error deleting mind map for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Failed to delete mind map", e);
        }
    }
}
