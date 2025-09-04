package com.microservice.resource_service.service;

import com.microservice.resource_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQService {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;

    public void sendMessageAsync(Object message) {
        CompletableFuture.runAsync(() -> rabbitTemplate.convertAndSend(rabbitMQConfig.getTopicExchangeName(), rabbitMQConfig.getRoutingKey(), message))
                .exceptionally(ex -> {
                    log.error("Failed to add message to queue!", ex);
                    return null;
                });
    }
}
