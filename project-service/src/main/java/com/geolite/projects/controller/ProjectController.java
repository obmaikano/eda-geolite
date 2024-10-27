package com.geolite.projects.controller;

import com.geolite.projects.command.CreateProjectCommand;
import com.geolite.projects.command.DeleteProjectCommand;
import com.geolite.projects.command.ProjectCommandHandler;
import com.geolite.projects.command.UpdateProjectCommand;
import com.geolite.projects.model.Project;
import com.geolite.projects.model.ProjectReadModel;
import com.geolite.projects.query.ProjectQueryHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/v1/projects")
public class ProjectController {

    private final ProjectCommandHandler commandHandler;
    private final ProjectQueryHandler queryHandler;

    @Autowired
    public ProjectController(ProjectCommandHandler commandHandler, ProjectQueryHandler queryHandler) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody CreateProjectCommand command) {
        try {
            Project project = commandHandler.handleCreateProject(command);
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable UUID projectId, @RequestBody UpdateProjectCommand command) {
        try {
            command.setProjectId(projectId);
            commandHandler.handleUpdateProject(command);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllProjects() {
        List<ProjectReadModel> projects = queryHandler.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProject(@PathVariable UUID projectId) {
        try {
            ProjectReadModel project = queryHandler.getProjectById(projectId);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID projectId) {
        try {
            DeleteProjectCommand command = new DeleteProjectCommand(projectId);
            commandHandler.handleDeleteProject(command);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
