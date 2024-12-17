package org.example.mindmappingsoftware.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Node {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "mind_map_id")
    private MindMap mindMap;
    private String content;
    private double xPosition;
    private double yPosition;
    private final Date creationDate;

    public Node() {
        this.creationDate = new Date();
    }

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
