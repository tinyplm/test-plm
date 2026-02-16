package org.acme.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.dto.LineDTO;
import org.acme.entity.Line;

@ApplicationScoped
public class LineMapper {

    public Line toEntity(LineDTO.Create request) {
        if (request == null) return null;
        Line line = new Line();
        line.lineCode = request.lineCode();
        line.seasonCode = request.seasonCode();
        line.year = request.year();
        line.brandId = request.brandId();
        line.marketId = request.marketId();
        line.channelId = request.channelId();
        line.startDate = request.startDate();
        line.endDate = request.endDate();
        line.plannedStyleCount = request.plannedStyleCount();
        line.plannedUnits = request.plannedUnits();
        line.plannedRevenue = request.plannedRevenue();
        return line;
    }

    public void updateEntity(Line entity, LineDTO.Update request) {
        if (entity == null || request == null) return;
        entity.lineCode = request.lineCode();
        entity.seasonCode = request.seasonCode();
        entity.year = request.year();
        entity.brandId = request.brandId();
        entity.marketId = request.marketId();
        entity.channelId = request.channelId();
        entity.startDate = request.startDate();
        entity.endDate = request.endDate();
        entity.plannedStyleCount = request.plannedStyleCount();
        entity.plannedUnits = request.plannedUnits();
        entity.plannedRevenue = request.plannedRevenue();
    }

    public LineDTO.Response toResponse(Line entity) {
        if (entity == null) return null;
        return new LineDTO.Response(
            entity.id,
            entity.version,
            entity.lineCode,
            entity.seasonCode,
            entity.year,
            entity.brandId,
            entity.marketId,
            entity.channelId,
            entity.startDate,
            entity.endDate,
            entity.plannedStyleCount,
            entity.plannedUnits,
            entity.plannedRevenue,
            entity.createdBy,
            entity.createdAt,
            entity.updatedBy,
            entity.updatedAt
        );
    }
}
