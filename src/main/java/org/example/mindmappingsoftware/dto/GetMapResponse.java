package org.example.mindmappingsoftware.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mindmappingsoftware.mementos.MindMapMemento;
import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.Node;

import java.util.List;

public class GetMapResponse {
    private MindMap mindMap;
    private List<Node> nodes;

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

    public MindMapMemento saveState() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String snapshot = mapper.writeValueAsString(this);
        return new MindMapMemento(snapshot);
    }

    public void restoreState(MindMapMemento memento) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        GetMapResponse restored = mapper.readValue(memento.getSnapshot(), GetMapResponse.class);
        this.mindMap = restored.getMindMap();
        this.nodes = restored.getNodes();
    }
}
