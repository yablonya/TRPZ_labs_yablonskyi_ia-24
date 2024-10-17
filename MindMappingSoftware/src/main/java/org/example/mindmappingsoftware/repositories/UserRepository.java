package org.example.mindmappingsoftware.repositories;

import org.example.mindmappingsoftware.models.User;

public interface UserRepository extends Repository<User, Long> {
    User findByEmail(String email);
}
