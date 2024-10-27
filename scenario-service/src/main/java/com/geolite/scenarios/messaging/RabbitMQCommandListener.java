package com.geolite.scenarios.messaging;

import com.geolite.scenarios.command.CreateScenarioCommand;
import com.geolite.scenarios.command.UpdateScenarioCommand;
import com.geolite.scenarios.command.DeleteScenarioCommand;
import com.geolite.scenarios.command.ScenarioCommandHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQCommandListener {

    @Autowired
    private ScenarioCommandHandler commandHandler;

    @RabbitListener(queues = "create-scenario-queue")
    public void handleCreateScenario(CreateScenarioCommand command) {
        commandHandler.handleCreateScenario(command);
    }

    @RabbitListener(queues = "update-scenario-queue")
    public void handleUpdateScenario(UpdateScenarioCommand command) {
        commandHandler.handleUpdateScenario(command);
    }

    @RabbitListener(queues = "delete-scenario-queue")
    public void handleDeleteScenario(DeleteScenarioCommand command) {
        commandHandler.handleDeleteScenario(command);
    }

}
