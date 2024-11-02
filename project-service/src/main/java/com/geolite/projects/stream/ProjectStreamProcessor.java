package com.geolite.projects.stream;

import com.geolite.projects.event.ProjectCreatedEvent;
//import com.geolite.projects.event.ProjectUpdatedEvent;

import com.geolite.projects.event.ProjectUpdatedEvent;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * A Kafka Streams processor that consumes events from "project-created" and "project-updated" topics,
 * logs information about the processed events, and handles invalid events.
 *
 * This processor uses Avro serialization and is designed to be used in a Spring-based application.
 *
 * @author Obakeng Maikano
 * @since 1.0.0
 */
@Component
@Slf4j
public class ProjectStreamProcessor {

    /**
     * Builds a Kafka Streams pipeline that consumes events from "project-created" and "project-updated" topics,
     * logs information about the processed events, and handles invalid events.
     *
     * @param streamsBuilder the Kafka Streams builder used to construct the pipeline
     */
    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        try {
            // Define serdes for events
            SpecificAvroSerde<ProjectCreatedEvent> projectCreatedEventSerde = new SpecificAvroSerde<>();
            projectCreatedEventSerde.configure(Collections.singletonMap("schema.registry.url", "http://localhost:18081"), false);
            SpecificAvroSerde<ProjectUpdatedEvent> projectUpdatedEventSerde = new SpecificAvroSerde<>();
            projectUpdatedEventSerde.configure(Collections.singletonMap("schema.registry.url", "http://localhost:18081"), false);

            // Create a stream from the "project-created" topic
            KStream<String, ProjectCreatedEvent> projectCreatedStream = streamsBuilder.stream(
                    "project-created",
                    Consumed.with(Serdes.String(), projectCreatedEventSerde)
            );

            // Create a stream from the "project-updated" topic
            KStream<String, ProjectUpdatedEvent> projectUpdatedStream = streamsBuilder.stream(
                    "project-updated",
                    Consumed.with(Serdes.String(), projectUpdatedEventSerde)
            );

            // Process the created projects
            projectCreatedStream.foreach((key, value) -> {
                if (value != null && value.getProjectId() != null) {
                    log.info("Project created: {}", value.getProjectId());
                } else {
                    log.warn("Received null or invalid ProjectCreatedEvent");
                }
            });

            // Process the updated projects
            projectUpdatedStream.foreach((key, value) -> {
                if (value != null && value.getProjectId() != null) {
                    log.info("Project updated: {}", value.getProjectId());
                } else {
                    log.warn("Received null or invalid ProjectUpdatedEvent");
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
