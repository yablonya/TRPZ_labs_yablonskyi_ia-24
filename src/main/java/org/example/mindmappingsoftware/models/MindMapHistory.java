package org.example.mindmappingsoftware.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MindMapHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mind_map_id", nullable = false)
    private MindMap mindMap;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String snapshot;

    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    // Геттери та сеттери
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MindMap getMindMap() {
        return mindMap;
    }

    public void setMindMap(MindMap mindMap) {
        this.mindMap = mindMap;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
}

