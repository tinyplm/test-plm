package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.entity.Line;
import org.acme.repository.LineRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LineService extends AbstractCrudService<Line> {

    private static final Logger LOG = Logger.getLogger(LineService.class);

    @Inject
    public LineService(LineRepository lineRepository) {
        super(lineRepository, LOG);
    }

    @Override
    protected void validateForCreate(Line line) {
        if (line.lineCode == null || line.lineCode.isBlank()) {
            throw new IllegalArgumentException("Line code is required.");
        }
    }

    @Override
    protected void applyUpdates(Line existing, Line updateData) {
        existing.lineCode = updateData.lineCode;
        existing.seasonCode = updateData.seasonCode;
        existing.year = updateData.year;
        existing.brandId = updateData.brandId;
        existing.marketId = updateData.marketId;
        existing.channelId = updateData.channelId;
        existing.startDate = updateData.startDate;
        existing.endDate = updateData.endDate;
        existing.plannedStyleCount = updateData.plannedStyleCount;
        existing.plannedUnits = updateData.plannedUnits;
        existing.plannedRevenue = updateData.plannedRevenue;
    }

    @Override
    protected String entityLabel() {
        return "LINE";
    }
}
