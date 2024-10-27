package com.geolite.drilling.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessControlListener {

    @RabbitListener(queues = "drilling-queue")
    public void receiveMessage(String message) {
        // Process control logic here
        System.out.println("Received message: " + message);
    }
}
