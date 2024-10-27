package com.geolite.drilling.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "project_read_model")
@AllArgsConstructor
@NoArgsConstructor
public class ProjectReadModel {

    @Id
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
    private String createdBy;
    private Timestamp createdDate;
    private String modifiedBy;
    private Timestamp modifiedDate;
}
