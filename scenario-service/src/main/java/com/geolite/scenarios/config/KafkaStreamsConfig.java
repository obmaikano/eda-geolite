package com.geolite.scenarios.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {

    /**
     * A Spring Bean definition for the default Kafka Streams configuration.
     *
     * <p>This configuration is used by the {@link KafkaStreams} instance created by the
     * {@link EnableKafkaStreams @EnableKafkaStreams} annotation.
     *
     * <p>The configuration is supplied with the following properties:
     * <ul>
     * <li>{@link StreamsConfig#APPLICATION_ID_CONFIG application.id}: {@code "project-streams"}
     * <li>{@link StreamsConfig#BOOTSTRAP_SERVERS_CONFIG bootstrap.servers}: {@code "localhost:9092"}
     * <li>{@link StreamsConfig#DEFAULT_KEY_SERDE_CLASS_CONFIG default.key.serde}: {@link Serdes#String() String}
     * <li>{@link StreamsConfig#DEFAULT_VALUE_SERDE_CLASS_CONFIG default.value.serde}: {@link SpecificAvroSerde}
     * <li>{@link KafkaAvroDeserializerConfig#SCHEMA_REGISTRY_URL_CONFIG schema.registry.url}: {@code "http://localhost:18081"}
     * </ul>
     *
     **/
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "project-streams");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:18081"); // Corrected URL
        return new KafkaStreamsConfiguration(props);
    }

}
