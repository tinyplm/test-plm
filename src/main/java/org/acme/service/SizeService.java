package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.acme.entity.Size;
import org.acme.repository.SizeRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SizeService {

    private static final Logger LOG = Logger.getLogger(SizeService.class);

    @Inject
    SizeRepository sizeRepository;

    public List<Size> list() {
        return sizeRepository.listAll();
    }

    public Size findById(UUID id) {
        return sizeRepository.findById(id);
    }

    @Transactional
    public Size create(Size size) {
        if (size == null || size.name == null || size.name.isBlank()) {
            throw new IllegalArgumentException("Size name is required.");
        }
        if (size.sizes == null || size.sizes.isBlank()) {
            throw new IllegalArgumentException("Sizes value is required.");
        }
        size.id = null;
        sizeRepository.persist(size);
        LOG.infof("SIZE_CREATED id=%s name=%s", size.id, size.name);
        return size;
    }

    @Transactional
    public Size update(UUID id, Size updateData, long version) {
        if (updateData == null) {
            throw new IllegalArgumentException("Size payload is required.");
        }
        Size existing = sizeRepository.findById(id);
        if (existing == null) {
            return null;
        }
        if (existing.version != version) {
            throw new jakarta.persistence.OptimisticLockException("Version mismatch. Expected " + version + " but found " + existing.version);
        }
        existing.name = updateData.name;
        existing.sizes = updateData.sizes;
        LOG.infof("SIZE_UPDATED id=%s", existing.id);
        return existing;
    }

    @Transactional
    public boolean delete(UUID id) {
        boolean deleted = sizeRepository.deleteById(id);
        if (deleted) {
            LOG.infof("SIZE_DELETED id=%s", id);
        }
        return deleted;
    }
}
