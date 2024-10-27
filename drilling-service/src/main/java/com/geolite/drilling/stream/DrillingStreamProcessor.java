package com.geolite.drilling.stream;

import com.geolite.drilling.event.DrillingCompletedEvent;
import com.geolite.drilling.event.DrillingStartedEvent;
import com.geolite.drilling.event.DrillingUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DrillingStreamProcessor {

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        try {
            // Define serdes for events
            Serde<DrillingStartedEvent> drillingCreatedEventSerde = new JsonSerde<>(DrillingStartedEvent.class);
            Serde<DrillingUpdatedEvent> drillingUpdatedEventSerde = new JsonSerde<>(DrillingUpdatedEvent.class);
            Serde<DrillingCompletedEvent> drillingCompletedEventSerde = new JsonSerde<>(DrillingCompletedEvent.class);

            // Create a stream from the "drilling-started" topic
            KStream<String, DrillingStartedEvent> drillingStartedStream = streamsBuilder.stream(
                    "drilling-started",
                    Consumed.with(Serdes.String(), drillingCreatedEventSerde)
            );

            // Create a stream from the "drilling-updated" topic
            KStream<String, DrillingUpdatedEvent> drillingUpdatedStream = streamsBuilder.stream(
                    "drilling-updated",
                    Consumed.with(Serdes.String(), drillingUpdatedEventSerde)
            );

            // Create a stream from the "drilling-completed" topic
            KStream<String, DrillingCompletedEvent> drillingCompletedStream = streamsBuilder.stream(
                    "drilling-completed",
                    Consumed.with(Serdes.String(), drillingCompletedEventSerde)
            );

            // Process the started drillings
            drillingStartedStream.foreach((key, value) -> {
                if (value != null && value.getHoleId() != null) {
                    log.info("Drilling started: {}", value.getHoleId());
                } else {
                    log.warn("Received null or invalid DrillingStartedEvent");
                }
            });

            // Process the updated drillings
            drillingUpdatedStream.foreach((key, value) -> {
                if (value != null && value.getHoleId() != null) {
                    log.info("Drilling updated: {}", value.getHoleId());
                } else {
                    log.warn("Received null or invalid DrillingUpdatedEvent");
                }
            });

            // Process the completed drillings
            drillingCompletedStream.foreach((key, value) -> {
                if (value != null && value.getHoleId() != null) {
                    log.info("Drilling completed: {}", value.getHoleId());
                } else {
                    log.warn("Received null or invalid DrillingCompletedEvent");
                }
            });

            log.info("Kafka Streams topology built successfully");
        } catch (Exception e) {
            log.error("Error building Kafka Streams topology", e);
            throw new RuntimeException("Failed to build Kafka Streams topology", e);
        }
    }
}
