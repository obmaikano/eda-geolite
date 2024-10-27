package com.geolite.projects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geolite.projects.command.CreateProjectCommand;
import com.geolite.projects.command.ProjectCommandHandler;
import com.geolite.projects.command.UpdateProjectCommand;
import com.geolite.projects.model.Project;
import com.geolite.projects.model.ProjectReadModel;
import com.geolite.projects.model.Status;
import com.geolite.projects.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = { "project-created", "project-updated", "project-deleted" })
public class ProjectServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectCommandHandler commandHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        projectRepository.deleteAll();
        createTestProject();
        createTestProject();
        createTestProject();
    }

    @Test
    public void testCreateProject() throws Exception {
        LocalDate today = LocalDate.now(); // Use LocalDate for date only
        Date startDate = Date.valueOf(today);

        LocalDate endDate = today.plusDays(30); // Add 30 days using LocalDate
        Date futureDate = Date.valueOf(endDate);

        // First, create a project
        CreateProjectCommand projectEvent = new CreateProjectCommand(
                "Test Project",
                "Test Location",
                "Gold",
                startDate,
                futureDate,
                "Project Description",
                "John Doe",
                Status.ACTIVE,
                "Test User");

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void testUpdateProject() throws Exception {
        Project project = createTestProject();

        // Update the project
        UpdateProjectCommand command = new UpdateProjectCommand(
                project.getProjectId(),
                "Test Project",
                "Test Location",
                "Gold",
                Date.valueOf(LocalDate.now()),
                Date.valueOf(LocalDate.now().plusDays(30)),
                "Project Description",
                "John Doe",
                Status.ON_HOLD,
                "Test User",
                Timestamp.from(Instant.now()));

        mockMvc.perform(put("/api/v1/projects/" + project.getProjectId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());

        // Verify that the project was updated
        Project updatedProject = projectRepository.findById(project.getProjectId()).orElse(null);
        assertNotNull(updatedProject);
        assertEquals("Test Project", updatedProject.getProjectName());
    }

    @Test
    public void testDeleteProject() throws Exception {
        Project project = createTestProject();

        mockMvc.perform(delete("/api/v1/projects/" + project.getProjectId()))
                .andExpect(status().isOk());

        // Verify that the project was deleted
        assertFalse(projectRepository.existsById(project.getProjectId()));
    }

    @Test
    public void testGetProject() throws Exception {
        LocalDate today = LocalDate.now(); // Use LocalDate for date only
        Date startDate = Date.valueOf(today);

        LocalDate endDate = today.plusDays(30); // Add 30 days using LocalDate
        Date futureDate = Date.valueOf(endDate);

        // First, create a project
        CreateProjectCommand projectCommand = new CreateProjectCommand(
                "Test Project",
                "Test Location",
                "Gold",
                startDate,
                futureDate,
                "Project Description",
                "John Doe",
                Status.ACTIVE,
                "Test User");

        Project project = commandHandler.handleCreateProject(projectCommand);

        // Wait for the project to be created
        Thread.sleep(1000);

        // Now get the project
        mockMvc.perform(get("/api/v1/projects/" + project.getProjectId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.projectId").value(project.getProjectId().toString()))
                .andExpect(jsonPath("$.projectName").value("Test Project"));
    }

    @Test
    public void testGetAllProjects() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<ProjectReadModel> projects = objectMapper.readValue(content, new TypeReference<List<ProjectReadModel>>() {});

        assertEquals(3, projects.size());
    }

    @Test
    public void testCreateProjectWithInvalidData() throws Exception {
        CreateProjectCommand command = new CreateProjectCommand(
                null,
                "Test Location",
                null,
                Date.valueOf(LocalDate.now()),
                Date.valueOf(LocalDate.now().plusDays(30)),
                "Project Description",
                "John Doe",
                Status.ACTIVE,
                "Test User");

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$").value("Project name cannot be empty.")
                );
    }

    @Test
    public void testUpdateNonExistentProject() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        UpdateProjectCommand command = new UpdateProjectCommand(
                nonExistentId,
                "Updated Project",
                "Updated Location",
                "Updated Commodity",
                Date.valueOf(LocalDate.now()),
                Date.valueOf(LocalDate.now().plusDays(30)),
                "Updated Description",
                "Updated User",
                Status.ON_HOLD,
                "Updated User",
                Timestamp.from(Instant.now()));

        mockMvc.perform(put("/api/v1/projects/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$").value("Project not found")
                );
    }

    @Test
    public void testDeleteNonExistentProject() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/projects/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    private Project createTestProject() {
        Project project = new Project();
        project.setProjectId(UUID.randomUUID());
        project.setProjectName("Test Project");
        project.setLocation("Test Location");
        project.setCommodity("Gold");
        project.setStartDate(Date.valueOf(LocalDate.now()));
        project.setEndDate(Date.valueOf(LocalDate.now().plusDays(30)));
        project.setDescription("Project Description");
        project.setCreatedBy("John Doe");
        project.setProjectLead("Mary Doe");
        project.setStatus(Status.ACTIVE);
        return projectRepository.save(project);
    }
}
