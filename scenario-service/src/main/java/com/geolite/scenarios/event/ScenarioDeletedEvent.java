package com.geolite.scenarios.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioDeletedEvent extends Event {
    private UUID scenarioId;
}
