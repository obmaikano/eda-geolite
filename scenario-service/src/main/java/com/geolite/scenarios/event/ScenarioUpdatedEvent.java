package com.geolite.scenarios.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioUpdatedEvent extends Event {

    private  UUID scenarioId;
    private  String scenarioName;
    private  String target;
    private  String methods;
    private  BigDecimal budget;
    private  String modifiedBy;
    private  Timestamp modifiedDate;
}
