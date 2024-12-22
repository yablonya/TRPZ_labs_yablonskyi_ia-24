package org.example.mindmappingsoftware.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mindmappingsoftware.mementos.MindMapMemento;
import org.example.mindmappingsoftware.models.*;

import java.time.LocalDateTime;
import java.util.List;

public class FullMindMap {
    private String snapshotId;
    private MindMap mindMap;
    private List<Node> nodes;
    private List<Connection> connections;
    private List<Icon> icons;
    private List<File> files;
    private LocalDateTime savedAt;

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public MindMap getMindMap() {
        return mindMap;
    }

    public void setMindMap(MindMap mindMap) {
        this.mindMap = mindMap;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public List<Icon> getIcons() {
        return icons;
    }

    public void setIcons(List<Icon> icons) {
        this.icons = icons;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public MindMapMemento saveState() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String snapshot = mapper.writeValueAsString(this);
        return new MindMapMemento(snapshot);
    }

    public void restoreState(MindMapMemento memento) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FullMindMap restored = mapper.readValue(memento.snapshot(), FullMindMap.class);
        this.snapshotId = restored.getSnapshotId();
        this.mindMap = restored.getMindMap();
        this.nodes = restored.getNodes();
        this.connections = restored.getConnections();
        this.icons = restored.getIcons();
        this.files = restored.getFiles();
    }
}
