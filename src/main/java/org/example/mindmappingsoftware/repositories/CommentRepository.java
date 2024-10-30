package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.Comment;
import org.example.mindmappingsoftware.models.MindMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMindMap(MindMap mindMap);
}
