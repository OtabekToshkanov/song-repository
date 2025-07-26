package com.microservice.resource_service.service;

import com.microservice.resource_service.dto.SongDto;
import com.microservice.resource_service.excpetion.InternalServerErrorException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SongIntegrationService {
    private final LoadBalancerClient loadBalancerClient;
    private final RestClient restClient;

    @Data
    public static class AddSongResponse {
        private int id;
    }

    @Data
    public static class DeleteSongResponse {
        private List<Integer> ids;
    }

    public int addSong(SongDto songDto) {
        AddSongResponse songServiceResponse = restClient.post()
                .uri(getSongServiceUri() + "/songs")
                .body(songDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new InternalServerErrorException("Error occurred when creating song");
                }))
                .body(AddSongResponse.class);

        assert songServiceResponse != null;
        return songServiceResponse.getId();
    }

    public List<Integer> deleteSongById(List<Integer> ids) {
        var queryParam = String.join(",", ids.stream().map(String::valueOf).toList());

        DeleteSongResponse deleteSongResponse = restClient.delete()
                .uri(getSongServiceUri() + "/songs?id=" + queryParam)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new InternalServerErrorException("Error occurred when deleting song");
                }))
                .body(DeleteSongResponse.class);

        assert deleteSongResponse != null;
        return deleteSongResponse.getIds();
    }

    private String getSongServiceUri() {
        var songInstance = loadBalancerClient.choose("song-service");

        if (songInstance == null) {
            throw new InternalServerErrorException("Error occurred when creating song. Song service is not available");
        }

        return songInstance.getUri().toString();
    }
}
