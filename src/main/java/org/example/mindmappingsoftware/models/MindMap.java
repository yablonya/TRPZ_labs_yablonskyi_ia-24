package org.example.mindmappingsoftware.models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
public class MindMap {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    private String name;
    private final Date creationDate;

    public MindMap() {
        this.id = UUID.randomUUID().toString();
        this.creationDate = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
