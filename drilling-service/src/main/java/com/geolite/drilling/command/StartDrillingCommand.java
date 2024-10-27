package com.geolite.drilling.command;

import com.geolite.drilling.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
public class StartDrillingCommand {
    private UUID projectId;
    private UUID scenarioId;
    private String holeName;
    private double collarEasting;
    private double collarNorthing;
    private double depth;
    private Status status;
    private Timestamp startTime;
    private String createdBy;
    private Timestamp createdDate;
}
