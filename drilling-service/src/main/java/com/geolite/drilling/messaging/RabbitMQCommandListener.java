package com.geolite.drilling.messaging;

import com.geolite.drilling.command.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQCommandListener {

    @Autowired
    private DrillingCommandHandler commandHandler;

    @RabbitListener(queues = "start-drilling-queue")
    public void handleStartDrilling(StartDrillingCommand command) {
        commandHandler.handleStartDrilling(command);
    }

//    @RabbitListener(queues = "update-drilling-queue")
//    public void handleUpdateDrilling(UpdateDrillingCommand command) {
//        commandHandler.handleUpdateDrilling(command);
//    }
//
//    @RabbitListener(queues = "delete-drilling-queue")
//    public void handleDeleteDrilling(DeleteDrillingCommand command) {
//        commandHandler.handleDeleteDrilling(command);
//    }
}
