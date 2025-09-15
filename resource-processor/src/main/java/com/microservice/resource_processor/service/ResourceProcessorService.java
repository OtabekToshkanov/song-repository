package com.microservice.resource_processor.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceProcessorService {
    private final ResourceIntegrationService resourceIntegrationService;
    private final SongIntegrationService songIntegrationService;
    private final Mp3ParserService mp3ParserService;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public void processResource(int resourceId) {
        CompletableFuture.supplyAsync(() -> resourceIntegrationService.loadResource(resourceId), executor)
                .thenApply(mp3ParserService::readSongMetadata)
                .thenAccept(resourceMetadata -> {
                    resourceMetadata.setId(resourceId);
                    songIntegrationService.addSong(resourceMetadata);
                })
                .exceptionally(ex -> {
                    log.error("Error while processing resource with ID {}", resourceId, ex);
                    return null;
                });
    }

    @PreDestroy
    public void cleanUp() {
        executor.close();
    }
}
