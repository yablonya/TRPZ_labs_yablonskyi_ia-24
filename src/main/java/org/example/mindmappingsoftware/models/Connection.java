package org.example.mindmappingsoftware.models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "connections")
public class Connection {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "mind_map_id")
    private MindMap mindMap;
    @ManyToOne
    @JoinColumn(name = "from_node_id")
    private Node fromNode;
    @ManyToOne
    @JoinColumn(name = "to_node_id")
    private Node toNode;
    private final Date creationDate;

    public Connection() {
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

    public Node getFromNode() {
        return fromNode;
    }

    public void setFromNode(Node fromNode) {
        this.fromNode = fromNode;
    }

    public Node getToNode() {
        return toNode;
    }

    public void setToNode(Node toNode) {
        this.toNode = toNode;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
