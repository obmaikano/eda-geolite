package com.geolite.scenarios.command;

import com.geolite.scenarios.event.ScenarioCreatedEvent;
import com.geolite.scenarios.eventstore.EventStore;
import com.geolite.scenarios.repository.ProjectReadModelRepository;
import com.geolite.scenarios.repository.ScenarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScenarioCommandHandlerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ProjectReadModelRepository projectReadModelRepository;

    @Mock
    private EventStore eventStore;

    @Mock
    private ScenarioRepository scenarioRepository;

    @InjectMocks
    private ScenarioCommandHandler scenarioCommandHandler;

    @Test
    public void handleCreateScenario() {
        UUID projectId = UUID.randomUUID();
        CreateScenarioCommand command = new CreateScenarioCommand(
                projectId,
                "Test Scenario",
                "Test Target",
                "Test Methods",
                BigDecimal.valueOf(1000),
                "Test User"
        );

        when(projectReadModelRepository.existsById(projectId)).thenReturn(true);

        scenarioCommandHandler.handleCreateScenario(command);
        verify(eventStore).saveEvent(any(ScenarioCreatedEvent.class));
        verify(kafkaTemplate).send(eq("scenario-created"), any(ScenarioCreatedEvent.class));
    }
}
