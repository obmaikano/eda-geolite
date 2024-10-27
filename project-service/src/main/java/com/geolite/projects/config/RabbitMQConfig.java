package com.geolite.projects.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue createProjectQueue() {
        return new Queue("create-project-queue", true);
    }

    @Bean
    public Queue updateProjectQueue() {
        return new Queue("update-project-queue", true);
    }

    @Bean
    public Queue deleteProjectQueue() {
        return new Queue("delete-project-queue", true);
    }

    @Bean
    public Queue dlxRoutingQueue() {
        return new Queue("dlx-routing-key", true);
    }

    @Bean
    public Queue projectQueue() {
        return new Queue("project-queue", true);
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
