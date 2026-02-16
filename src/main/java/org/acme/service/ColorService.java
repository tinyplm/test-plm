package org.acme.service;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.acme.entity.Color;
import org.acme.repository.ColorRepository;

@ApplicationScoped
public class ColorService {

    @Inject
    ColorRepository colorRepository;

    @CacheResult(cacheName = "color-cache")
    public List<Color> list() {
        return colorRepository.listAll();
    }

    @CacheResult(cacheName = "color-cache")
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
    @CacheInvalidate(cacheName = "color-cache")
    public Color update(UUID id, Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Color payload is required.");
        }
        Color existing = colorRepository.findById(id);
        if (existing == null) {
            return null;
        }
        existing.name = color.name;
        existing.description = color.description;
        existing.rgb = color.rgb;
        return existing;
    }

    @Transactional
    @CacheInvalidate(cacheName = "color-cache")
    public boolean delete(UUID id) {
        return colorRepository.deleteById(id);
    }
}
