package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.entity.Color;
import org.acme.repository.ColorRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ColorService extends AbstractCrudService<Color> {

    private static final Logger LOG = Logger.getLogger(ColorService.class);

    @Inject
    public ColorService(ColorRepository colorRepository) {
        super(colorRepository, LOG);
    }

    @Override
    protected void validateForCreate(Color color) {
        if (color.name == null || color.name.isBlank()) {
            throw new IllegalArgumentException("Color name is required.");
        }
        if (color.rgb == null || color.rgb.isBlank()) {
            throw new IllegalArgumentException("Color RGB is required.");
        }
    }

    @Override
    protected void applyUpdates(Color existing, Color updateData) {
        existing.name = updateData.name;
        existing.description = updateData.description;
        existing.rgb = updateData.rgb;
    }

    @Override
    protected String entityLabel() {
        return "COLOR";
    }
}
