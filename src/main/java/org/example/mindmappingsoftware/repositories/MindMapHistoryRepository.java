package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.MindMapSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MindMapHistoryRepository extends JpaRepository<MindMapSnapshot, String> {
    MindMapSnapshot findByMindMapIdAndSavedAt(String mindMapId, LocalDateTime restoreDate);
    List<MindMapSnapshot> findAllByMindMapId(String mindMapId);
    List<MindMapSnapshot> findAllByMindMapIdOrderBySavedAtDesc(String mindMapId);
    void deleteAllByMindMapId(String mindMapId);
}
