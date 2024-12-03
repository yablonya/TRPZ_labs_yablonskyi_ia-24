package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.icons.Icon;
import org.example.mindmappingsoftware.models.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IconRepository extends JpaRepository<Icon, Long> {
    List<Icon> findByNode(Node node);
}
