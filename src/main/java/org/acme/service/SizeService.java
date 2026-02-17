package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.entity.Size;
import org.acme.repository.SizeRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SizeService extends AbstractCrudService<Size> {

    private static final Logger LOG = Logger.getLogger(SizeService.class);

    @Inject
    public SizeService(SizeRepository sizeRepository) {
        super(sizeRepository, LOG);
    }

    @Override
    protected void validateForCreate(Size size) {
        if (size.name == null || size.name.isBlank()) {
            throw new IllegalArgumentException("Size name is required.");
        }
        if (size.sizes == null || size.sizes.isBlank()) {
            throw new IllegalArgumentException("Sizes value is required.");
        }
    }

    @Override
    protected void applyUpdates(Size existing, Size updateData) {
        existing.name = updateData.name;
        existing.sizes = updateData.sizes;
    }

    @Override
    protected String entityLabel() {
        return "SIZE";
    }
}
