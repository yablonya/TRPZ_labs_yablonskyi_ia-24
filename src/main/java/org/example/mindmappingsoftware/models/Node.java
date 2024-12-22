package org.example.mindmappingsoftware.models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
public class Node {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "mind_map_id")
    private MindMap mindMap;
    private String content;
    private double xPosition;
    private double yPosition;
    private final Date creationDate;

    public Node() {
        this.id = UUID.randomUUID().toString();
        this.creationDate = new Date();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getXPosition() {
        return xPosition;
    }

    public void setXPosition(double xPosition) {
        this.xPosition = xPosition;
    }

    public double getYPosition() {
        return yPosition;
    }

    public void setYPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
