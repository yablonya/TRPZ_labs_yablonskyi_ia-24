package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.MindMapHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MindMapHistoryRepository extends JpaRepository<MindMapHistory, String> {
    MindMapHistory findByMindMapIdAndSavedAt(String mindMapId, LocalDateTime restoreDate);
    List<MindMapHistory> findAllByMindMapId(String mindMapId);
    List<MindMapHistory> findAllByMindMapIdOrderBySavedAtDesc(String mindMapId);
    void deleteAllByMindMapId(String mindMapId);
}
