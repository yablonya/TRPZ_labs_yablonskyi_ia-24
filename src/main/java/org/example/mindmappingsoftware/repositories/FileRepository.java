package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.File;
import org.example.mindmappingsoftware.models.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByNode(Node node);
    void deleteByNode(Node node);
}
