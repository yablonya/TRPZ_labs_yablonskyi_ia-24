package org.example.mindmappingsoftware.models;

import java.util.Date;

public class Comment {
    private Long id;
    private MindMap mindMap;
    private User author;
    private String content;
    private final Date creationDate;

    public Comment() {
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
