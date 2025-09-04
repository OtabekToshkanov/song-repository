package com.microservice.resource_processor.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.default-queue}")
    private String queueName;
}
