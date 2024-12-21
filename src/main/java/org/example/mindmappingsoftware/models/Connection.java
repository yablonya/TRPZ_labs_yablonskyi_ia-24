package org.example.mindmappingsoftware.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "from_node_id")
    private Node fromNode;
    @ManyToOne
    @JoinColumn(name = "to_node_id")
    private Node toNode;
    private final Date creationDate;

    public Connection() {
        this.creationDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
