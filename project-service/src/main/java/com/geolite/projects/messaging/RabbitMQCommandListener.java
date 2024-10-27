package com.geolite.projects.messaging;

import com.geolite.projects.command.CreateProjectCommand;
import com.geolite.projects.command.DeleteProjectCommand;
import com.geolite.projects.command.ProjectCommandHandler;
import com.geolite.projects.command.UpdateProjectCommand;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQCommandListener {

    @Autowired
    private ProjectCommandHandler commandHandler;

    @RabbitListener(queues = "create-project-queue")
    public void handleCreateProject(CreateProjectCommand command) {
        commandHandler.handleCreateProject(command);
    }

    @RabbitListener(queues = "update-project-queue")
    public void handleUpdateProject(UpdateProjectCommand command) {
        commandHandler.handleUpdateProject(command);
    }

    @RabbitListener(queues = "delete-project-queue")
    public void handleDeleteProject(DeleteProjectCommand command) {
        commandHandler.handleDeleteProject(command);
    }
}
