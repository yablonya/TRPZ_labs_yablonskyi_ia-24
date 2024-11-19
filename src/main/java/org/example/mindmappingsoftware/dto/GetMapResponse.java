package org.example.mindmappingsoftware.dto;

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
}
