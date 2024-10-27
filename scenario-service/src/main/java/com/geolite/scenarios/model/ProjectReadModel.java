package com.geolite.scenarios.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String createdBy;
    private Timestamp createdDate;
    private String modifiedBy;
    private Timestamp modifiedDate;
}
