package org.acme.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.dto.ColorDTO;
import org.acme.entity.Color;

@ApplicationScoped
public class ColorMapper {

    public Color toEntity(ColorDTO.Create request) {
        if (request == null) return null;
        Color color = new Color();
        color.name = request.name();
        color.description = request.description();
        color.rgb = request.rgb();
        return color;
    }

    public void updateEntity(Color entity, ColorDTO.Update request) {
        if (entity == null || request == null) return;
        entity.name = request.name();
        entity.description = request.description();
        entity.rgb = request.rgb();
    }

    public ColorDTO.Response toResponse(Color entity) {
        if (entity == null) return null;
        return new ColorDTO.Response(
            entity.id,
            entity.version,
            entity.name,
            entity.description,
            entity.rgb,
            entity.createdBy,
            entity.createdAt,
            entity.updatedBy,
            entity.updatedAt
        );
    }
}
