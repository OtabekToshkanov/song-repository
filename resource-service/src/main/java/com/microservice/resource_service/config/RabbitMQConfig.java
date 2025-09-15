package com.microservice.resource_service.config;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

@Configuration
@Getter
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.default-topic}")
    private String topicExchangeName;
    @Value("${spring.rabbitmq.default-queue}")
    private String queueName;
    @Value("${spring.rabbitmq.default-routing-key}")
    private String routingKey;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);

        rabbitAdmin.setAutoStartup(true);

        return rabbitAdmin;
    }

    @Bean
    public TopicExchange topicExchange(RabbitAdmin rabbitAdmin) {
        TopicExchange topicExchange = new TopicExchange(topicExchangeName, true, false);
        rabbitAdmin.declareExchange(topicExchange);
        return topicExchange;
    }

    @Bean
    public Queue queue(RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue(queueName, true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    public Binding binding(RabbitAdmin rabbitAdmin, Queue queue, TopicExchange topicExchange) {
        Binding binding = BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setRetryTemplate(createRetryTemplate());

        return rabbitTemplate;
    }

    private RetryTemplate createRetryTemplate() {
        RetryTemplate retryTemplate = new org.springframework.retry.support.RetryTemplate();

        RetryPolicy retryPolicy = new SimpleRetryPolicy(3, Map.of(Exception.class, true)); // 5 retries for all exceptions
        retryTemplate.setRetryPolicy(retryPolicy);

        retryTemplate.setBackOffPolicy(new ExponentialBackOffPolicy() {
            {
                setInitialInterval(1000);
                setMultiplier(3.0);
            }
        });

        return retryTemplate;
    }
}
