package com.geolite.drilling.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class EventProcessingTracker {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String EVENT_KEY_PREFIX = "processed_event:";
    private static final long EVENT_EXPIRY = 7; // days

    public boolean isEventProcessed(UUID eventId) {
        Boolean exists = redisTemplate.hasKey(EVENT_KEY_PREFIX + eventId);
        return exists != null && exists;
    }

    public void markEventAsProcessed(UUID eventId) {
        redisTemplate.opsForValue().set(EVENT_KEY_PREFIX + eventId, "processed", EVENT_EXPIRY, TimeUnit.DAYS);
    }
}
