package com.geolite.scenarios.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "scenarios")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scenario {

    @Id
    private UUID scenarioId;

    @Column(nullable = false)
    private UUID projectId;

    @Column(nullable = false, unique = true)
    private String scenarioName;

    private String target;
    private String methods;

    @Column(precision = 10, scale = 2)
    private BigDecimal budget;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private Timestamp createdDate;

    private String modifiedBy;
    private Timestamp modifiedDate;
}
