package com.geolite.projects.stream;

import com.geolite.projects.event.ProjectCreatedEvent;
//import com.geolite.projects.event.ProjectUpdatedEvent;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class ProjectStreamProcessor {

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        try {
            // Define serdes for events
            SpecificAvroSerde<ProjectCreatedEvent> projectCreatedEventSerde = new SpecificAvroSerde<>();
            projectCreatedEventSerde.configure(Collections.singletonMap("schema.registry.url", "http://localhost:18081"), false);

            // Create a stream from the "project-created" topic
            KStream<String, ProjectCreatedEvent> projectCreatedStream = streamsBuilder.stream(
                    "project-created",
                    Consumed.with(Serdes.String(), projectCreatedEventSerde)
            );

            // Process the created projects
            projectCreatedStream.foreach((key, value) -> {
                if (value != null && value.getProjectId() != null) {
                    log.info("Project created: {}", value.getProjectId());
                } else {
                    log.warn("Received null or invalid ProjectCreatedEvent");
                }
            });

            log.info("Kafka Streams topology built successfully");
        } catch (Exception e) {
            log.error("Error building Kafka Streams topology", e);
            throw new RuntimeException("Failed to build Kafka Streams topology", e);
        }
    }
}
