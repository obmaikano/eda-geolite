package com.geolite.scenarios.util;

import com.geolite.projects.model.Status;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

public class ConversionUtil {

     // Convert Date to long (milliseconds since epoch)
    public static long dateToLong(Date date) {
        return date != null ? date.getTime() : 0;
    }

    // Convert long (milliseconds since epoch) to Date
    public static Date longToDate(long millis) {
        return new Date(millis);
    }

    // Convert Timestamp to long (milliseconds since epoch)
    public static long timestampToLong(Timestamp timestamp) {
        return timestamp != null ? timestamp.getTime() : 0;
    }

    // Convert long (milliseconds since epoch) to Timestamp
    public static Timestamp longToTimestamp(long millis) {
        return new Timestamp(millis);
    }

    // Convert String to UUID
    public static UUID stringToUUID(String uuidString) {
        if (uuidString == null || uuidString.isEmpty()) {
            throw new IllegalArgumentException("UUID string cannot be null or empty");
        }
        return UUID.fromString(uuidString);
    }

    // Convert UUID to String
    public static String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    // Convert Status to String
    public static String statusToString(Status status) {
        return status != null ? status.name() : null; // Convert enum to string
    }

    // Convert String to Status
    public static Status stringToStatus(String statusString) {
        if (statusString == null || statusString.isEmpty()) {
            throw new IllegalArgumentException("Status string cannot be null or empty");
        }
        return Status.fromString(statusString); // Convert string to enum
    }
}
