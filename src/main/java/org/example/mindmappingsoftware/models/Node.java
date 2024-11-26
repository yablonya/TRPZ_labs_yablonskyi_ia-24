package org.example.mindmappingsoftware.models;

import jakarta.persistence.*;
import org.example.mindmappingsoftware.models.icons.Icon;

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
    private String xPosition;
    private String yPosition;
    private String iconType;
    private String iconValue;
    @Transient
    private Icon icon;
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

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    public String getIconValue() {
        return iconValue;
    }

    public void setIconValue(String iconValue) {
        this.iconValue = iconValue;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        this.iconType = icon.getType();
        this.iconValue = icon.getInfo();
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
