package org.acme.service;

import io.quarkus.hibernate.panache.PanacheRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.acme.entity.CoreEntity;
import org.jboss.logging.Logger;

public abstract class AbstractCrudService<E extends CoreEntity> {

    private final PanacheRepository.Managed<E, UUID> repository;
    private final Logger logger;

    protected AbstractCrudService(PanacheRepository.Managed<E, UUID> repository, Logger logger) {
        this.repository = Objects.requireNonNull(repository, "repository is required");
        this.logger = Objects.requireNonNull(logger, "logger is required");
    }

    public List<E> list() {
        return repository.listAll();
    }

    public E findById(UUID id) {
        if (id == null) {
            return null;
        }
        return repository.findById(id);
    }

    @Transactional
    public E create(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException(entityLabel() + " payload is required.");
        }
        validateForCreate(entity);
        entity.id = null;
        repository.persist(entity);
        logInfo(entityLabel() + "_CREATED id=%s", entity.id);
        return entity;
    }

    @Transactional
    public E update(UUID id, E updateData, long version) {
        if (updateData == null) {
            throw new IllegalArgumentException(entityLabel() + " payload is required.");
        }

        E existing = repository.findById(id);
        if (existing == null) {
            return null;
        }

        if (existing.version != version) {
            throw new OptimisticLockException(
                    "Version mismatch. Expected " + version + " but found " + existing.version
            );
        }

        validateForUpdate(updateData);
        applyUpdates(existing, updateData);
        logInfo(entityLabel() + "_UPDATED id=%s", existing.id);
        return existing;
    }

    @Transactional
    public boolean delete(UUID id) {
        boolean deleted = repository.deleteById(id);
        if (deleted) {
            logInfo(entityLabel() + "_DELETED id=%s", id);
        }
        return deleted;
    }

    protected void validateForCreate(E entity) {
        // Override in concrete services when create validation is needed.
    }

    protected void validateForUpdate(E updateData) {
        // Override in concrete services when update validation is needed.
    }

    protected abstract void applyUpdates(E existing, E updateData);

    protected String entityLabel() {
        return "ENTITY";
    }

    protected PanacheRepository.Managed<E, UUID> repository() {
        return repository;
    }

    protected void logInfo(String pattern, Object... args) {
        logger.infof(pattern, args);
    }
}
