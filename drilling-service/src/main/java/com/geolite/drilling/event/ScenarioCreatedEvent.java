package com.geolite.drilling.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioCreatedEvent extends Event {
    private  UUID scenarioId;
    private  UUID projectId;
    private  String scenarioName;
    private  String target;
    private  String methods;
    private  BigDecimal budget;
    private  String createdBy;
    private  Timestamp createdDate;
}
