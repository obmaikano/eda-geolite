package com.geolite.projects.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DeleteProjectCommand {
    private UUID projectId;
}
