package com.geolite.scenarios.query;

import com.geolite.scenarios.model.Scenario;
import com.geolite.scenarios.model.ScenarioReadModel;
import com.geolite.scenarios.repository.ScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScenarioQueryHandler {

    private final ScenarioRepository scenarioRepository;

    @Autowired
    public ScenarioQueryHandler(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    public ScenarioReadModel getScenarioById(UUID scenarioId) {
        return scenarioRepository.findById(scenarioId)
                .map(this::mapToReadModel)
                .orElseThrow(() -> new RuntimeException("Scenario not found"));
    }

    public List<ScenarioReadModel> getAllScenarios() {
        return scenarioRepository.findAll().stream()
                .map(this::mapToReadModel)
                .collect(Collectors.toList());
    }

    private ScenarioReadModel mapToReadModel(Scenario scenario) {
        // Implement mapping from Scenario to ScenarioReadModel
        return new ScenarioReadModel(
                scenario.getScenarioId(),
                scenario.getProjectId(),
                scenario.getScenarioName(),
                scenario.getTarget(),
                scenario.getMethods(),
                scenario.getBudget(),
                scenario.getCreatedBy(),
                scenario.getModifiedBy(),
                scenario.getCreatedDate(),
                scenario.getModifiedDate()
        );
    }
}
