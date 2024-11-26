package org.example.mindmappingsoftware.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mindmappingsoftware.mementos.MindMapMemento;
import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.Node;

import java.time.LocalDateTime;
import java.util.List;

public class FullMindMap {
    private MindMap mindMap;
    private List<Node> nodes;
    private LocalDateTime savedAt;

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
        FullMindMap restored = mapper.readValue(memento.getSnapshot(), FullMindMap.class);
        this.mindMap = restored.getMindMap();
        this.nodes = restored.getNodes();
    }
}
