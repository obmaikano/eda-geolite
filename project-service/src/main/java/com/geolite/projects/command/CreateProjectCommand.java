package com.geolite.projects.command;

import com.geolite.projects.model.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class CreateProjectCommand {
    private String projectName;
    private String location;
    private String commodity;
    private Date startDate;
    private Date endDate;
    private String description;
    private String projectLead;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String createdBy;
}
