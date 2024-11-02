package com.geolite.projects.util;

import com.geolite.projects.model.Project;
import com.geolite.projects.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectValidator {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    public void validateNewProject(Project project) {
        validateMandatoryFields(project);
        validateUniqueName(project);
        validateProjectLead(project);
        validateDates(project);
    }
    
    private void validateMandatoryFields(Project project) {
        if (project.getProjectName() == null || project.getProjectName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (project.getLocation() == null || project.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Project location is required");
        }
        if (project.getCommodity() == null || project.getCommodity().trim().isEmpty()) {
            throw new IllegalArgumentException("Commodity type is required");
        }
        if (project.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (project.getDescription() == null || project.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Project description is required");
        }
    }
    
    private void validateUniqueName(Project project) {
        if (projectRepository.existsByProjectName(project.getProjectName())) {
            throw new IllegalArgumentException("Project name must be unique");
        }
    }
    
    private void validateProjectLead(Project project) {
        if (project.getProjectLead() == null || project.getProjectLead().trim().isEmpty()) {
            throw new IllegalArgumentException("Project lead must be assigned");
        }
    }
    
    private void validateDates(Project project) {
        if (project.getEndDate() != null && project.getEndDate().before(project.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }
}
