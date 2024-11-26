package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.MindMapHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MindMapHistoryRepository extends JpaRepository<MindMapHistory, Long> {
    MindMapHistory findByMindMapIdAndSavedAt(Long mindMapId, LocalDateTime restoreDate);

    List<MindMapHistory> findAllByMindMapIdOrderBySavedAtDesc(Long mindMapId);
}
