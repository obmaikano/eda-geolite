package com.geolite.drilling.event;

import com.geolite.drilling.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DrillingCompletedEvent extends Event{
    private UUID holeId;          // Unique identifier for the hole
    private UUID projectId;        // Project this drilling belongs to
    private UUID scenarioId;       // Scenario under which this drilling is performed
    private String holeName;       // Name of the drill hole
    private BigDecimal finalDepth; // Final depth of the drilling operation
    private Timestamp completionTime; // Timestamp when drilling was completed
    private String completedBy;    // Who completed the drilling
    private Status status;         // Status indicating completion
}
