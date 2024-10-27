package com.geolite.projects.command;

import com.geolite.projects.model.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UpdateProjectCommand {
    private UUID projectId;
    private String projectName;
    private String location;
    private String commodity;
    private Date startDate;
    private Date endDate;
    private String description;
    private String projectLead;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String modifiedBy;
    private Timestamp modifiedDate;
}
