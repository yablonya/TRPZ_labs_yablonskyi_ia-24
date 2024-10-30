package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.Connection;
import org.example.mindmappingsoftware.models.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    List<Connection> findByFromNode(Node node);
    List<Connection> findByToNode(Node node);
}
