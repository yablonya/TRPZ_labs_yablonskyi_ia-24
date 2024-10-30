package org.example.mindmappingsoftware.strategies;

import org.example.mindmappingsoftware.models.File;
import org.example.mindmappingsoftware.models.Node;
import org.example.mindmappingsoftware.repositories.FileRepository;
import org.example.mindmappingsoftware.repositories.NodeRepository;

import java.util.List;

public class WithFilesProcessingStrategy implements NodeProcessingStrategy {
    private final NodeRepository nodeRepository;
    private final FileRepository fileRepository;

    public WithFilesProcessingStrategy(NodeRepository nodeRepository, FileRepository fileRepository) {
        this.nodeRepository = nodeRepository;
        this.fileRepository = fileRepository;
    }

    @Override
    public void process(Node node) {}

    @Override
    public void process(Node node, List<File> nodeFiles) {
        nodeRepository.save(node);

        fileRepository.saveAll(nodeFiles);
    }
}

