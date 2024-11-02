package com.geolite.projects.util;

import com.geolite.projects.model.Status;
import org.springframework.stereotype.Component;

@Component
public class StatusTransitionValidator {
    
    public void validateStatusTransition(Status currentStatus, Status newStatus) {
        if (currentStatus == Status.DRAFT && newStatus == Status.ACTIVE) {
            // Can only move to ACTIVE if all required fields are present
            validateActiveTransition();
        }
        if (currentStatus == Status.COMPLETED && newStatus != Status.ARCHIVED) {
            throw new IllegalStateException("Completed projects can only transition to Archived status");
        }
        if (currentStatus == Status.ARCHIVED) {
            throw new IllegalStateException("Archived projects cannot change status");
        }
    }
    
    private void validateActiveTransition() {
        // Add additional validation logic for transitioning to ACTIVE status
    }
}