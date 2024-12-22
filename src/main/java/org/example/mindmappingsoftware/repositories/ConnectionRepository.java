package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.Connection;
import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, String> {
    List<Connection> findAllByMindMap(MindMap mindMap);
    List<Connection> findByFromNodeOrToNode(Node fromNode, Node toNode);
    boolean existsByFromNodeAndToNode(Node fromNode, Node toNode);
    @Modifying
    @Transactional
    void deleteAllByMindMap(MindMap mindMap);
}
