package com.geolite.scenarios.event;

import com.geolite.scenarios.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;


/**
 * Event that is published when a project is updated.
 *
 * @author Obakeng Maikano
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUpdatedEvent extends Event {

    private UUID projectId;
    private String projectName;
    private String location;
    private String commodity;
    private Date startDate;
    private Date endDate;
    private String description;
    private String projectLead;
    private Status status;
    private String modifiedBy;
    private Timestamp modifiedDate;

}