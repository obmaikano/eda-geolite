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
@Table(name = "drilling_read_model")
@AllArgsConstructor
@NoArgsConstructor
public class DrillingReadModel {
    @Id
    private UUID holeId;
    private UUID projectId;
    private UUID scenarioId;
    private String holeName;
    private double collarEasting;
    private double collarNorthing;
    private double depth;
    private Status status;
    private Timestamp startTime;
    private BigDecimal finalDepth;
    private Timestamp completionTime;
    private String completedBy;
    private String createdBy;
    private String modifiedBy;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
}
