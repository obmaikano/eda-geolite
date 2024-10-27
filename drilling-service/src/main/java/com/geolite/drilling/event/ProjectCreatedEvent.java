package com.geolite.drilling.event;

import com.geolite.drilling.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Event that is published when a new project is created.
 *
 * <p>This event is used by the {@link com.geolite.projects.service.ProjectService} to notify other services that a new project has been created.
 *
 * @author Obakeng Maikano
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectCreatedEvent extends Event {

    private UUID projectId;
    private String projectName;
    private String location;
    private String commodity;
    private Date startDate;
    private Date endDate;
    private String description;
    private String projectLead;
    private Status status;
    private String createdBy;
    private Timestamp createdDate;

}
