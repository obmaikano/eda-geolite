package com.geolite.scenarios.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue createScenarioQueue() {
        return new Queue("create-scenario-queue", true);
    }

    @Bean
    public Queue updateScenarioQueue() {
        return new Queue("update-scenario-queue", true);
    }

    @Bean
    public Queue deleteScenarioQueue() {
        return new Queue("delete-scenario-queue", true);
    }

    @Bean
    public Queue dlxRoutingQueue() {
        return new Queue("dlx-routing-key", true);
    }

    @Bean
    public Queue scenarioQueue() {
        return new Queue("scenario-queue", true);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
