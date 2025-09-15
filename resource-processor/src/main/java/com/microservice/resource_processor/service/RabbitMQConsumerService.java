package com.microservice.resource_processor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumerService {
    private final ResourceProcessorService resourceProcessorService;

    @RabbitListener(queues = "#{@rabbitMQConfig.getQueueName}")
    public void receiveMessage(int resourceId) {
        resourceProcessorService.processResource(resourceId);
    }
}
