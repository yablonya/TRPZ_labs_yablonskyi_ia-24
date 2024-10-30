package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.MindMap;
import org.example.mindmappingsoftware.models.User;

import java.util.List;

public interface MindMapRepository extends Repository<MindMap, Long> {
    List<MindMap> findByCreator(User user);
}
