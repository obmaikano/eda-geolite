package com.geolite.projects.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "projects")
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Project {

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
    private String modifiedBy;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
}
