package org.example.mindmappingsoftware.dto;

import org.example.mindmappingsoftware.models.File;

import java.util.List;

public class NodeCreationRequest {
    private String type;
    private String content;
    private String xPosition;
    private String yPosition;
    private String mindMapId;
    private List<File> nodeFiles;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getXPosition() {
        return xPosition;
    }

    public void setXPosition(String xPosition) {
        this.xPosition = xPosition;
    }

    public String getYPosition() {
        return yPosition;
    }

    public void setYPosition(String yPosition) {
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
