package com.geolite.projects.query;

import com.geolite.projects.model.Project;
import com.geolite.projects.model.ProjectReadModel;
import com.geolite.projects.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectQueryHandler {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectQueryHandler(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectReadModel getProjectById(UUID projectId) {
        return projectRepository.findById(projectId)
                .map(this::mapToReadModel)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public List<ProjectReadModel> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToReadModel)
                .collect(Collectors.toList());
    }

    private ProjectReadModel mapToReadModel(Project project) {
        // Implement mapping from Project to ProjectReadModel
        return new ProjectReadModel(
                project.getProjectId(),
                project.getProjectName(),
                project.getLocation(),
                project.getCommodity(),
                project.getStartDate(),
                project.getEndDate(),
                project.getDescription(),
                project.getProjectLead(),
                project.getStatus(),
                project.getCreatedBy(),
                project.getCreatedDate(),
                project.getModifiedBy(),
                project.getModifiedDate()
        );
    }
}
