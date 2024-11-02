package com.geolite.scenarios.stream;

import com.geolite.scenarios.event.ScenarioCreatedEvent;
import com.geolite.scenarios.event.ScenarioUpdatedEvent;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class ScenarioStreamProcessor {

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        try {
            // Define serdes for events
            SpecificAvroSerde<ScenarioCreatedEvent> scenarioCreatedSerde = new SpecificAvroSerde<>();
            scenarioCreatedSerde.configure(Collections.singletonMap("schema.registry.url", "http://localhost:18081"), false);
            SpecificAvroSerde<ScenarioUpdatedEvent> scenarioUpdatedSerde = new SpecificAvroSerde<>();
            scenarioUpdatedSerde.configure(Collections.singletonMap("schema.registry.url", "http://localhost:18081"), false);

            // Create a stream from the "scenario-created" topic
            KStream<String, ScenarioCreatedEvent> scenarioCreatedStream = streamsBuilder.stream(
                    "scenario-created",
                    Consumed.with(Serdes.String(), scenarioCreatedSerde)
            );

            // Create a stream from the "scenario-updated" topic
            KStream<String, ScenarioUpdatedEvent> scenarioUpdatedStream = streamsBuilder.stream(
                    "scenario-updated",
                    Consumed.with(Serdes.String(), scenarioUpdatedSerde)
            );

            // Process the created scenarios
            scenarioCreatedStream.foreach((key, value) -> {
                if (value != null && value.getScenarioId() != null) {
                    log.info("Scenario created: {}", value);
                }
            });

            // Process the updated scenarios
            scenarioUpdatedStream.foreach((key, value) -> {
                if (value != null && value.getScenarioId() != null) {
                    log.info("Scenario updated: {}", value);
                }
            });

            // Build the topology
            streamsBuilder.build();
            log.info("Kafka Streams topology built successfully");
        } catch (Exception e) {
            log.error("Error building Kafka Streams topology", e);
            throw new RuntimeException("Failed to build Kafka Streams topology", e);
        }
    }
}