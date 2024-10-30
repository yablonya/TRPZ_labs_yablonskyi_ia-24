package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.Connection;
import org.example.mindmappingsoftware.models.Node;

import java.util.List;

public interface ConnectionRepository extends Repository<Connection, Long> {
    List<Connection> findByFromNode(Node node);
    List<Connection> findByToNode(Node node);
}
