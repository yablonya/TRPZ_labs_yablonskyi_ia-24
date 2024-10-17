package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.Node;

import java.util.List;

public interface NodeRepository extends Repository<Node, Long> {
    List<Node> findByMindMap(MindMap mindMap);
}
