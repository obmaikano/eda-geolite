package com.geolite.projects.service;

import com.geolite.projects.model.Project;

import java.util.List;

public interface ProjectService {

    public List<Project> getAllProjects();

    public Project getProjectById(Long projectId);

    public Project createProject(Project project);

    public Project updateProject(Long id, Project project);

    public void deleteProject(Long projectId);
}
