package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.File;
import org.example.mindmappingsoftware.models.Node;

import java.util.List;

public interface FileRepository extends Repository<File, Long> {
    List<File> findByNode(Node node);
}
