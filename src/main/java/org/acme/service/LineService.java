package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.acme.entity.Line;
import org.acme.repository.LineRepository;

@ApplicationScoped
public class LineService {

    @Inject
    LineRepository lineRepository;

    public List<Line> list() {
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
        if (line.lineCode == null || line.lineCode.isBlank()) {
            throw new IllegalArgumentException("Line code is required.");
        }
        if (line.seasonCode == null || line.seasonCode.isBlank()) {
            throw new IllegalArgumentException("Season code is required.");
        }
        if (line.brandId == null) {
            throw new IllegalArgumentException("Brand id is required.");
        }
        if (line.marketId == null) {
            throw new IllegalArgumentException("Market id is required.");
        }
        if (line.channelId == null) {
            throw new IllegalArgumentException("Channel id is required.");
        }
        line.id = null;
        line.persist();
        return line;
    }

    @Transactional
    public Line update(UUID id, Line line) {
        if (line == null) {
            throw new IllegalArgumentException("Line payload is required.");
        }
        Line existing = lineRepository.findById(id);
        if (existing == null) {
            return null;
        }
        existing.lineCode = line.lineCode;
        existing.seasonCode = line.seasonCode;
        existing.year = line.year;
        existing.brandId = line.brandId;
        existing.marketId = line.marketId;
        existing.channelId = line.channelId;
        existing.startDate = line.startDate;
        existing.endDate = line.endDate;
        existing.plannedStyleCount = line.plannedStyleCount;
        existing.plannedUnits = line.plannedUnits;
        existing.plannedRevenue = line.plannedRevenue;
        existing.createdBy = line.createdBy;
        return existing;
    }

    @Transactional
    public boolean delete(UUID id) {
        return lineRepository.deleteById(id);
    }
}
