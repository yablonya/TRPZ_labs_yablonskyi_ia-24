package org.example.mindmappingsoftware.dto;

import java.util.List;

public class NodeCreationRequest {
    private String content;
    private double xPosition;
    private double yPosition;
    private String mindMapId;
    private List<NodeIcon> nodeIcons;
    private List<NodeFile> nodeFiles;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getxPosition() {
        return xPosition;
    }

    public void setxPosition(double xPosition) {
        this.xPosition = xPosition;
    }

    public double getyPosition() {
        return yPosition;
    }

    public void setyPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public String getMindMapId() {
        return mindMapId;
    }

    public void setMindMapId(String mindMapId) {
        this.mindMapId = mindMapId;
    }

    public List<NodeIcon> getNodeIcons() {
        return nodeIcons;
    }

    public void setNodeIcons(List<NodeIcon> nodeIcons) {
        this.nodeIcons = nodeIcons;
    }

    public List<NodeFile> getNodeFiles() {
        return nodeFiles;
    }

    public void setNodeFiles(List<NodeFile> nodeFiles) {
        this.nodeFiles = nodeFiles;
    }

}
