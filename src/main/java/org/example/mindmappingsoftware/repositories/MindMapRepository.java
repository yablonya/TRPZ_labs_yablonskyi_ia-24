package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MindMapRepository extends JpaRepository<MindMap, String> {
    List<MindMap> findByCreator(User user);
}
