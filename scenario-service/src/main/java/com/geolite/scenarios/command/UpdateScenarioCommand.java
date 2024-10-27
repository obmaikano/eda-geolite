package com.geolite.scenarios.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UpdateScenarioCommand {
    private UUID scenarioId;
    private String scenarioName;
    private String target;
    private String methods;
    private BigDecimal budget;
    private String modifiedBy;
}
