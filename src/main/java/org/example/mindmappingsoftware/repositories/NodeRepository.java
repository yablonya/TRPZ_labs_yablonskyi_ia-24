package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, String> {
    List<Node> findAllByMindMap(MindMap mindMap);

    @Modifying
    @Transactional
    void deleteAllByMindMap(MindMap mindMap);
}
