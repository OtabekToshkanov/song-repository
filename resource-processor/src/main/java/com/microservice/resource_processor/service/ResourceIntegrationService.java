package com.microservice.resource_processor.service;

import com.microservice.resource_processor.excpetion.InternalServerErrorException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class ResourceIntegrationService {
    private final LoadBalancerClient loadBalancerClient;
    private final RestClient restClient;

    @Retry(name = "resource-integration")
    public byte[] loadResource(int resourceId) {
        return restClient.get()
                .uri(getResourceServiceUri() + "/resources/" + resourceId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new InternalServerErrorException("Error occurred when downloading resource");
                }))
                .body(byte[].class);
    }

    private String getResourceServiceUri() {
        var resourceInstance = loadBalancerClient.choose("resource-service");

        if (resourceInstance == null) {
            throw new InternalServerErrorException("Error occurred when downloading resource. Resource service is not available");
        }

        return resourceInstance.getUri().toString();
    }
}
