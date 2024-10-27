package com.geolite.scenarios.stream;

import com.geolite.scenarios.event.ScenarioCreatedEvent;
import com.geolite.scenarios.event.ScenarioUpdatedEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ScenarioStreamProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioStreamProcessor.class);

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        try {
            // Define serdes for events
            Serde<ScenarioCreatedEvent> scenarioCreatedEventSerde = new JsonSerde<>(ScenarioCreatedEvent.class);
            Serde<ScenarioUpdatedEvent> scenarioUpdatedEventSerde = new JsonSerde<>(ScenarioUpdatedEvent.class);

            // Create a stream from the "scenario-created" topic
            KStream<String, ScenarioCreatedEvent> scenarioCreatedStream = streamsBuilder.stream(
                    "scenario-created",
                    Consumed.with(Serdes.String(), scenarioCreatedEventSerde)
            );

            // Create a stream from the "scenario-updated" topic
            KStream<String, ScenarioUpdatedEvent> scenarioUpdatedStream = streamsBuilder.stream(
                    "scenario-updated",
                    Consumed.with(Serdes.String(), scenarioUpdatedEventSerde)
            );

            // Process the created scenarios
            scenarioCreatedStream.foreach((key, value) -> {
                if (value != null && value.getScenarioId() != null) {
                    logger.info("Scenario created: {}", value.getScenarioId());
                } else {
                    logger.warn("Received null or invalid ScenarioCreatedEvent");
                }
            });

            // Process the updated scenarios
            scenarioUpdatedStream.foreach((key, value) -> {
                if (value != null && value.getScenarioId() != null) {
                    logger.info("Scenario updated: {}", value.getScenarioId());
                } else {
                    logger.warn("Received null or invalid ScenarioUpdatedEvent");
                }
            });

            logger.info("Kafka Streams topology built successfully");
        } catch (Exception e) {
            logger.error("Error building Kafka Streams topology", e);
            throw new RuntimeException("Failed to build Kafka Streams topology", e);
        }
    }
}