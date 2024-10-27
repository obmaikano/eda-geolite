package com.geolite.drilling.query;

import com.geolite.drilling.model.Drilling;
import com.geolite.drilling.model.DrillingReadModel;
import com.geolite.drilling.repository.DrillingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DrillingQueryHandler {

    private final DrillingRepository drillingRepository;

    @Autowired
    public DrillingQueryHandler(DrillingRepository drillingRepository) {
        this.drillingRepository = drillingRepository;
    }

    public DrillingReadModel getDrillingById(UUID holeId) {
        return drillingRepository.findById(holeId)
                .map(this::mapToReadModel)
                .orElseThrow(() -> new RuntimeException("Drilling not found"));
    }

    public List<DrillingReadModel> getAllDrillings() {
        return drillingRepository.findAll().stream()
                .map(this::mapToReadModel)
                .collect(Collectors.toList());
    }

    private DrillingReadModel mapToReadModel(Drilling drilling) {
        return new DrillingReadModel(
                drilling.getHoleId(),
                drilling.getProjectId(),
                drilling.getScenarioId(),
                drilling.getHoleName(),
                drilling.getCollarEasting(),
                drilling.getCollarNorthing(),
                drilling.getDepth(),
                drilling.getStatus(),
                drilling.getStartTime(),
                drilling.getFinalDepth(),
                drilling.getCompletionTime(),
                drilling.getCompletedBy(),
                drilling.getCreatedBy(),
                drilling.getModifiedBy(),
                drilling.getCreatedDate(),
                drilling.getModifiedDate()
        );
    }
}
