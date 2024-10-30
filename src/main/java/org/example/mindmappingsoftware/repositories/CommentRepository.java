package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.Comment;
import org.example.mindmappingsoftware.models.MindMap;

import java.util.List;

public interface CommentRepository extends Repository<Comment, Long>  {
    List<Comment> findByMindMap(MindMap mindMap);
}
