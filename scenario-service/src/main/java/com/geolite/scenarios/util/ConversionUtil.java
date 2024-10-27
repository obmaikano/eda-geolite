package com.geolite.scenarios.util;

import com.geolite.scenarios.model.Status;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
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

    public static ByteBuffer bigDecimalToByteBuffer(BigDecimal bigDecimal) {
        // Convert BigDecimal's BigInteger component and scale separately
        BigInteger unscaledValue = bigDecimal.unscaledValue();
        int scale = bigDecimal.scale();

        // Convert BigInteger to a byte array
        byte[] unscaledBytes = unscaledValue.toByteArray();

        // Allocate a ByteBuffer with enough space to hold the scale (4 bytes) and unscaled value
        ByteBuffer buffer = ByteBuffer.allocate(4 + unscaledBytes.length);

        // Put scale and unscaled value bytes into the buffer
        buffer.putInt(scale);
        buffer.put(unscaledBytes);

        // Flip the buffer to prepare for reading
        buffer.flip();

        return buffer;
    }

    public static BigDecimal byteBufferToBigDecimal(ByteBuffer budget) {
        // Read scale and unscaled value bytes from the buffer
        int scale = budget.getInt();
        byte[] unscaledBytes = new byte[budget.remaining()];
        budget.get(unscaledBytes);

        // Convert the byte array to a BigInteger
        BigInteger unscaledValue = new BigInteger(unscaledBytes);

        // Convert the BigInteger to a BigDecimal
        return new BigDecimal(unscaledValue, scale);
    }
}
