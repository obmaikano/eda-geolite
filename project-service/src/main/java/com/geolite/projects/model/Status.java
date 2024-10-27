package com.geolite.projects.model;

public enum Status {
    INITIATED,
    STARTED,
    COMPLETED,
    CANCELLED,
    DELETED,
    ON_HOLD,
    ACTIVE;

    public static Status fromString(String status) {
        return Status.valueOf(status.toUpperCase());
    }
}
