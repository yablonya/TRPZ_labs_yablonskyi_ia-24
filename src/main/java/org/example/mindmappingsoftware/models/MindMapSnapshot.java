package org.example.mindmappingsoftware.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mind_maps_snapshots")
public class MindMapSnapshot {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "mind_map_id", nullable = false)
    private MindMap mindMap;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String snapshot;

    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    public MindMapSnapshot() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
