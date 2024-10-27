package com.geolite.scenarios;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geolite.scenarios.event.ProjectCreatedEvent;
import com.geolite.scenarios.model.Status;
import com.geolite.scenarios.command.CreateScenarioCommand;
import com.geolite.scenarios.command.ScenarioCommandHandler;
import com.geolite.scenarios.command.UpdateScenarioCommand;
import com.geolite.scenarios.model.Scenario;
import com.geolite.scenarios.model.ScenarioReadModel;
import com.geolite.scenarios.repository.ScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
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
@EmbeddedKafka(partitions = 1, topics = { "scenario-created", "scenario-updated", "scenario-deleted" })
public class ScenarioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScenarioCommandHandler commandHandler;

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        scenarioRepository.deleteAll();
        createTestScenario();
        createTestScenario();
    }


    @Test
    public void testCreateScenario() throws Exception {
        LocalDate today = LocalDate.now(); // Use LocalDate for date only
        Date startDate = Date.valueOf(today);

        LocalDate endDate = today.plusDays(30); // Add 30 days using LocalDate
        Date futureDate = Date.valueOf(endDate);

        // First, create a project
        UUID projectId = UUID.randomUUID();
        ProjectCreatedEvent projectEvent = new ProjectCreatedEvent(projectId,
                "Test Project",
                "Test Location",
                "Gold",
                startDate,
                futureDate,
                "Description",
                "John Doe",
                Status.ACTIVE,
                "Test User",
                Timestamp.from(Instant.now()));
        kafkaTemplate.send("project-created", projectEvent);

        // Wait for the project event to be processed
        Thread.sleep(1000);

        CreateScenarioCommand command = new CreateScenarioCommand(
                projectId,
                "Test Scenario",
                "Test Target",
                "Test Methods",
                BigDecimal.valueOf(1000),
                "Test User"
        );

        mockMvc.perform(post("/api/v1/scenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isString());
    }

    @Test
    public void testUpdateScenario() throws Exception {
        Scenario scenario = createTestScenario();

        UpdateScenarioCommand command = new UpdateScenarioCommand(
                scenario.getScenarioId(), "Updated Scenario", "Updated Target", "Updated Methods", BigDecimal.valueOf(2000), "Updated User"
        );

        mockMvc.perform(put("/api/v1/scenarios/" + scenario.getScenarioId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());

        Scenario updatedScenario = scenarioRepository.findById(scenario.getScenarioId()).orElse(null);
        assertNotNull(updatedScenario);
        assertEquals("Updated Scenario", updatedScenario.getScenarioName());
    }

    @Test
    public void testDeleteScenario() throws Exception {
        Scenario scenario = createTestScenario();

        mockMvc.perform(delete("/api/v1/scenarios/" + scenario.getScenarioId()))
                .andExpect(status().isOk());

        assertFalse(scenarioRepository.existsById(scenario.getScenarioId()));
    }

    @Test
    public void testGetScenario() throws Exception {
        LocalDate today = LocalDate.now(); // Use LocalDate for date only
        Date startDate = Date.valueOf(today);

        LocalDate endDate = today.plusDays(30); // Add 30 days using LocalDate
        Date futureDate = Date.valueOf(endDate);

        // First, create a project
        UUID projectId = UUID.randomUUID();
        ProjectCreatedEvent projectEvent = new ProjectCreatedEvent(projectId,
                "Test Project",
                "Test Location",
                "Gold",
                startDate,
                futureDate,
                "Description",
                "John Doe",
                Status.ACTIVE,
                "Test User",
                Timestamp.from(Instant.now()));
        kafkaTemplate.send("project-events", projectEvent);

        // Wait for the project event to be processed
        Thread.sleep(1000);

        // Now create a scenario
        UUID scenarioId = UUID.randomUUID();
        CreateScenarioCommand command = new CreateScenarioCommand(
                projectId,
                "Test Scenario",
                "Test Target",
                "Test Methods",
                BigDecimal.valueOf(1000),
                "Test User"
        );

        commandHandler.handleCreateScenario(command);

        // Wait for the scenario to be created
        Thread.sleep(1000);

        mockMvc.perform(get("/api/v1/scenarios/" + scenarioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scenarioId").value(scenarioId.toString()))
                .andExpect(jsonPath("$.scenarioName").value("Test Scenario"));
    }

    @Test
    public void testGetAllScenarios() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/scenarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<ScenarioReadModel> scenarios = objectMapper.readValue(content, new TypeReference<List<ScenarioReadModel>>() {});

        assertEquals(2, scenarios.size());
    }

    @Test
    public void testCreateScenarioWithInvalidData() throws Exception {
        CreateScenarioCommand command = new CreateScenarioCommand(
                null, "", "", "", BigDecimal.valueOf(-1000), ""
        );

        mockMvc.perform(post("/api/v1/scenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateNonExistentScenario() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        UpdateScenarioCommand command = new UpdateScenarioCommand(
                nonExistentId, "Updated Scenario", "Updated Target", "Updated Methods", BigDecimal.valueOf(2000), "Updated User"
        );

        mockMvc.perform(put("/api/v1/scenarios/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNonExistentScenario() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/scenarios/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetNonExistentScenario() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/scenarios/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    private Scenario createTestScenario() {
        Scenario scenario = new Scenario();
        scenario.setScenarioId(UUID.randomUUID());
        scenario.setProjectId(UUID.randomUUID());
        scenario.setScenarioName("Test Scenario " + (scenarioRepository.count() + 1));
        scenario.setTarget("Test Target");
        scenario.setMethods("Test Methods");
        scenario.setBudget(BigDecimal.valueOf(1000));
        scenario.setCreatedDate(Timestamp.from(Instant.now()));
        scenario.setCreatedBy("Test User");
        return scenarioRepository.save(scenario);
    }

}
