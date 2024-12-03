package org.example.mindmappingsoftware.models.icons;

public class PriorityIcon implements Icon {
    private final int priorityLevel;

    public PriorityIcon(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    @Override
    public String getInfo() {
        return String.valueOf(priorityLevel);
    }

    @Override
    public String getType() {
        return "Priority";
    }
}
