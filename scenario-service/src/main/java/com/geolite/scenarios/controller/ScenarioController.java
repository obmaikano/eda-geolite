package com.geolite.scenarios.controller;

import com.geolite.scenarios.command.CreateScenarioCommand;
import com.geolite.scenarios.command.DeleteScenarioCommand;
import com.geolite.scenarios.command.ScenarioCommandHandler;
import com.geolite.scenarios.command.UpdateScenarioCommand;
import com.geolite.scenarios.model.Scenario;
import com.geolite.scenarios.model.ScenarioReadModel;
import com.geolite.scenarios.query.ScenarioQueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/scenarios")
public class ScenarioController {

    private final ScenarioCommandHandler commandHandler;
    private final ScenarioQueryHandler queryHandler;

    @Autowired
    public ScenarioController(ScenarioCommandHandler commandHandler, ScenarioQueryHandler queryHandler) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
    }

    @PostMapping
    public ResponseEntity<?> createScenario(@RequestBody CreateScenarioCommand command) {
        try {
            Scenario scenario = commandHandler.handleCreateScenario(command);
            return ResponseEntity.ok(scenario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{scenarioId}")
    public ResponseEntity<?> updateScenario(@PathVariable UUID scenarioId, @RequestBody UpdateScenarioCommand command) {
        try {
            command.setScenarioId(scenarioId);
            commandHandler.handleUpdateScenario(command);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{scenarioId}")
    public ResponseEntity<?> deleteScenario(@PathVariable UUID scenarioId) {
        try {
            commandHandler.handleDeleteScenario(new DeleteScenarioCommand(scenarioId));
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{scenarioId}")
    public ResponseEntity<?> getScenario(@PathVariable UUID scenarioId) {
        try {
            ScenarioReadModel scenario = queryHandler.getScenarioById(scenarioId);
            return ResponseEntity.ok(scenario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllScenarios() {
        List<ScenarioReadModel> scenarios = queryHandler.getAllScenarios();
        return ResponseEntity.ok(scenarios);
    }
}