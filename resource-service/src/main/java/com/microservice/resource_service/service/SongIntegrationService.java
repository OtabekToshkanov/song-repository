package com.microservice.resource_service.service;

import com.microservice.resource_service.dto.SongDto;
import com.microservice.resource_service.excpetion.InternalServerErrorException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SongIntegrationService {
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
                .uri("/songs")
                .body(songDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((_, _) -> {
                    throw new InternalServerErrorException("Error occurred when creating song");
                }))
                .body(AddSongResponse.class);

        assert songServiceResponse != null;
        return songServiceResponse.getId();
    }

    public List<Integer> deleteSongById(List<Integer> ids) {
        var queryParam = String.join(",", ids.stream().map(String::valueOf).toList());

        DeleteSongResponse deleteSongResponse = restClient.delete()
                .uri("/songs?id=" + queryParam)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((_, _) -> {
                    throw new InternalServerErrorException("Error occurred when deleting song");
                }))
                .body(DeleteSongResponse.class);

        assert deleteSongResponse != null;
        return deleteSongResponse.getIds();
    }
}
