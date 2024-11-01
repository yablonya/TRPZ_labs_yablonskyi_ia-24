package org.example.mindmappingsoftware.strategies;

import org.example.mindmappingsoftware.models.File;
import org.example.mindmappingsoftware.models.Node;

import java.util.List;

public interface NodeProcessingStrategy {
    void process(Node node);
    void process(Node node, List<File> nodeFiles);
}

