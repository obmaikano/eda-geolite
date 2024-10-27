package com.geolite.drilling.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue startDrillingQueue() {
        return new Queue("start-drilling-queue", true);
    }

    @Bean
    public Queue updateDrillingQueue() {
        return new Queue("update-drilling-queue", true);
    }

    @Bean
    public Queue deleteDrillingQueue() {
        return new Queue("delete-drilling-queue", true);
    }

    @Bean
    public Queue dlxRoutingQueue() {
        return new Queue("dlx-routing-key", true);
    }

    @Bean
    public Queue drillingQueue() {
        return new Queue("drilling-queue", true);
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
