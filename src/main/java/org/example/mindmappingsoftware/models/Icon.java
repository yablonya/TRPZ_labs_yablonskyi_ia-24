package org.example.mindmappingsoftware.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Icon {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "node_id")
    private Node node;
    private String type;
    private String content;

    public Icon() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

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
}
