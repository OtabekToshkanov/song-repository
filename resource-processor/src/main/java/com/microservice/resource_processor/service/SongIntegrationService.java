package com.microservice.resource_processor.service;

import com.microservice.resource_processor.dto.SongDto;
import com.microservice.resource_processor.excpetion.InternalServerErrorException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongIntegrationService {
    private final LoadBalancerClient loadBalancerClient;
    private final RestClient restClient;

    @Data
    public static class AddSongResponse {
        private int id;
    }

    @Retry(name = "song-integration", fallbackMethod = "addSongFallback")
    public void addSong(SongDto songDto) {
        AddSongResponse songServiceResponse = restClient.post()
                .uri(getSongServiceUri() + "/songs")
                .body(songDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    System.out.println("A"+new String(response.getBody().readAllBytes()));
                    System.out.println(response.getStatusCode());
                    throw new InternalServerErrorException("Error occurred when creating song");
                }))
                .body(AddSongResponse.class);

        assert songServiceResponse != null;
    }

    public void addSongFallback(SongDto songDto, Exception ex) {
        log.error("Failed to add song", ex);
    }

    private String getSongServiceUri() {
        var songInstance = loadBalancerClient.choose("song-service");

        if (songInstance == null) {
            throw new InternalServerErrorException("Error occurred when creating song. Song service is not available");
        }

        return songInstance.getUri().toString();
    }
}
