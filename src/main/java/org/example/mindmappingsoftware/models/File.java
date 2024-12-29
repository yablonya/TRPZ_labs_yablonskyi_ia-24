package org.example.mindmappingsoftware.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "files")
public class File {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "node_id")
    private Node node;
    private String name;
    private String url;
    private String type;

    public File() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String filePath) {
        this.url = filePath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
