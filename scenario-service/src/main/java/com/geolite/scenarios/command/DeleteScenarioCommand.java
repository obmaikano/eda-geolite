package com.geolite.scenarios.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DeleteScenarioCommand {
    private UUID scenarioId;
}
