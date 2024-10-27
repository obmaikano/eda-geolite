package com.geolite.drilling.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "scenario_read_model")
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioReadModel {

    @Id
    private UUID scenarioId;
    private UUID projectId;
    private String scenarioName;
    private String target;
    private String method;
    private BigDecimal budget;
    private String createdBy;
    private String modifiedBy;
    private Timestamp createdDate;
    private Timestamp modifiedDate;

}
