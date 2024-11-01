package org.example.mindmappingsoftware.strategies;

import org.example.mindmappingsoftware.models.File;
import org.example.mindmappingsoftware.models.Node;
import org.example.mindmappingsoftware.repositories.NodeRepository;

import java.util.List;

public class WithoutFilesProcessingStrategy implements NodeProcessingStrategy {
    private final NodeRepository nodeRepository;

    public WithoutFilesProcessingStrategy(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Override
    public void process(Node node) {
        nodeRepository.save(node);
    }

    @Override
    public void process(Node node, List<File> nodeFiles) {}
}

