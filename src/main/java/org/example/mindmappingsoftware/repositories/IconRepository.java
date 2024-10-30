package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.Icon;
import org.example.mindmappingsoftware.models.Node;

import java.util.List;

public interface IconRepository extends Repository<Icon, Long> {
    List<Icon> findByNode(Node node);
}
