package org.example.mindmappingsoftware.models.icons;

public class CategoryIcon implements Icon {
    private final String category;

    public CategoryIcon(String category) {
        this.category = category;
    }

    @Override
    public String getInfo() {
        return category;
    }

    @Override
    public String getType() {
        return "Category";
    }
}
