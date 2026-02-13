package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.quarkus.security.identity.SecurityIdentity;
import java.util.List;
import java.util.UUID;
import org.acme.entity.Color;
import org.acme.repository.ColorRepository;

@ApplicationScoped
public class ColorService {

    @Inject
    ColorRepository colorRepository;

    @Inject
    SecurityIdentity securityIdentity;

    public List<Color> list() {
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
        String actor = currentActor();
        color.createdBy = actor;
        color.updatedBy = actor;
        colorRepository.persist(color);
        return color;
    }

    @Transactional
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
        existing.updatedBy = currentActor();
        return existing;
    }

    @Transactional
    public boolean delete(UUID id) {
        return colorRepository.deleteById(id);
    }

    private String currentActor() {
        if (securityIdentity == null || securityIdentity.isAnonymous()) {
            return "system";
        }
        String principalName = securityIdentity.getPrincipal().getName();
        if (principalName == null || principalName.isBlank()) {
            return "system";
        }
        return principalName;
    }
}
