package org.example.mindmappingsoftware.repositories;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRepository<T, ID> implements Repository<T, ID> {
    @Override
    public List<T> findAll() {
        return new ArrayList<>();
    }

    @Override
    public T findById(ID id) {
        return null;
    }

    @Override
    public void save(T entity) {
    }

    @Override
    public void update(T entity) {
    }

    @Override
    public void deleteById(ID id) {
    }
}
