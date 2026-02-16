package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Id;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import org.acme.entity.Color;
import org.acme.repository.ColorRepository;

@ApplicationScoped
public class ColorService {

    @Inject
    ColorRepository colorRepository;

    public java.util.List<Color> list() {
        return colorRepository.listAll();
    }

    public Color findById(UUID id) {
        return colorRepository.findById(id);
    }

    @Transactional
    public Color create(Color color) {

        if (color == null || color.name == null || color.name.isBlank()) {
            throw new IllegalArgumentException("Color name is required.");
        }
        if (color.rgb == null || color.rgb.isBlank()) {
            throw new IllegalArgumentException("Color RGB is required.");
        }
        color.id = null;
        colorRepository.persist(color);
        return color;
    }

    @Transactional
    public Color update(UUID id, Color updateData, long version) {
        if (updateData == null) {
            throw new IllegalArgumentException("Color payload is required.");
        }
        Color existing = colorRepository.findById(id);
        if (existing == null) {
            return null;
        }
        if (existing.version != version) {
            throw new OptimisticLockException("Version mismatch. Expected " + version + " but found " + existing.version);
        }
        existing.name = updateData.name;
        existing.description = updateData.description;
        existing.rgb = updateData.rgb;
        return existing;
    }

    @Transactional
    public boolean delete(UUID id) {
        return colorRepository.deleteById(id);
    }
}