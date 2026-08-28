package it.univr.DiabetesLogger.service;

import java.util.Optional;

public interface CrudService<Entity> {

    Entity create(Entity entity);

    Iterable<Entity> getAll();

    Optional<Entity> getById(Integer id);

    Entity update(Integer id, Entity entity);

    void delete(Integer id);
}
