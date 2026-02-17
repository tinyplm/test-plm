package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import java.util.UUID;

import org.acme.entity.Color;
import org.acme.entity.Line;
import org.acme.repository.LineRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LineService {

    private static final Logger LOG = Logger.getLogger(LineService.class);

    @Inject
    LineRepository lineRepository;

    public java.util.List<Line> list() {
        return lineRepository.listAll();
    }

    public Line findById(UUID id) {
        return lineRepository.findById(id);
    }

    @Transactional
    public Line create(Line line) {
        if (line == null) {
            throw new IllegalArgumentException("Line payload is required.");
        }
        // Basic validation is handled by DTO constraints, but business logic validation can go here.
        if (line.lineCode == null || line.lineCode.isBlank()) {
            throw new IllegalArgumentException("Line code is required.");
        }
        
        line.id = null;
        lineRepository.persist(line);
        LOG.infof("LINE_CREATED id=%s lineCode=%s", line.id, line.lineCode);
        return line;
    }

    @Transactional
    public Line update(UUID id, Line updateData, long version) {
        if (updateData == null) {
            throw new IllegalArgumentException("Line payload is required.");
        }
        Line existing = lineRepository.findById(id);
        if (existing == null) {
            return null;
        }
        if (existing.version != version) {
            throw new OptimisticLockException("Version mismatch. Expected " + version + " but found " + existing.version);
        }
        
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
        
        LOG.infof("LINE_UPDATED id=%s", existing.id);
        return existing;
    }

    @Transactional
    public boolean delete(UUID id) {
        boolean deleted = lineRepository.deleteById(id);
        if (deleted) {
            LOG.infof("LINE_DELETED id=%s", id);
        }
        return deleted;
    }
}
