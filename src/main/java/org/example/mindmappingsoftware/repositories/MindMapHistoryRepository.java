package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.MindMapSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MindMapHistoryRepository extends JpaRepository<MindMapSnapshot, String> {
    List<MindMapSnapshot> findAllByMindMapId(String mindMapId);
    List<MindMapSnapshot> findAllByMindMapIdOrderBySavedAtDesc(String mindMapId);
    @Modifying
    @Transactional
    void deleteAllByMindMapId(String mindMapId);
}
