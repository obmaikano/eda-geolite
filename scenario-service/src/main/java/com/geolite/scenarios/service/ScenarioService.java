package com.geolite.scenarios.service;

import com.geolite.scenarios.model.Scenario;

import java.util.List;
import java.util.UUID;

public interface ScenarioService {
    Scenario createScenario(Scenario scenario);
    Scenario updateScenario(UUID scenarioId, Scenario scenario);
    void deleteScenario(UUID scenarioId);
    Scenario getScenarioById(UUID scenarioId);
    List<Scenario> getAllScenarios();
    List<Scenario> getScenariosByProjectId(Long projectId);
}
