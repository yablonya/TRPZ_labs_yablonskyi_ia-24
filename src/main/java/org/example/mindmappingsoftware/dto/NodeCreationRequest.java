package org.example.mindmappingsoftware.dto;

import org.example.mindmappingsoftware.models.File;

import java.util.List;

public class NodeCreationRequest {
    private String content;
    private double xPosition;
    private double yPosition;
    private String mindMapId;
    private List<File> nodeFiles;

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

    public List<File> getNodeFiles() {
        return nodeFiles;
    }

    public void setNodeFiles(List<File> nodeFiles) {
        this.nodeFiles = nodeFiles;
    }

}
