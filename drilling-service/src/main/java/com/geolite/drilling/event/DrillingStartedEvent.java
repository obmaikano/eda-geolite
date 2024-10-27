package com.geolite.drilling.event;

import com.geolite.drilling.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DrillingStartedEvent extends Event {
    private UUID holeId;
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
