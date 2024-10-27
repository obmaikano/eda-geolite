package com.geolite.drilling.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeadLetterListener {

    @RabbitListener(queues = "dlx-routing-key")
    public void handleDeadLetterQueue(String message) {
        // Log the error and handle the failed message
        log.error("Received message from DLQ: {}", message);
        // Implement logic to handle the failed message
    }
}
