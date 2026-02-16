package org.acme.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.dto.SizeDTO;
import org.acme.entity.Size;

@ApplicationScoped
public class SizeMapper {

    public Size toEntity(SizeDTO.Create request) {
        if (request == null) return null;
        Size size = new Size();
        size.name = request.name();
        size.sizes = request.sizes();
        return size;
    }

    public void updateEntity(Size entity, SizeDTO.Update request) {
        if (entity == null || request == null) return;
        entity.name = request.name();
        entity.sizes = request.sizes();
    }

    public SizeDTO.Response toResponse(Size entity) {
        if (entity == null) return null;
        return new SizeDTO.Response(
            entity.id,
            entity.version,
            entity.name,
            entity.sizes,
            entity.createdBy,
            entity.createdAt,
            entity.updatedBy,
            entity.updatedAt
        );
    }
}
