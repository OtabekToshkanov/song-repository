package com.microservice.resource_service.service;

import com.microservice.resource_service.dto.SongDto;
import com.microservice.resource_service.excpetion.BadRequestException;
import com.microservice.resource_service.excpetion.InternalServerErrorException;
import com.microservice.resource_service.excpetion.ResourceNotFoundException;
import com.microservice.resource_service.model.Resource;
import com.microservice.resource_service.repository.ResourceRepository;
import com.microservice.resource_service.util.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final Validator validator;
    private final ResourceRepository repository;
    private final Mp3ParserService mp3ParserService;
    private final SongIntegrationService songIntegrationService;

    @Transactional
    public Integer createResource(String contentType, byte[] body) {
        if (!"audio/mpeg".equals(contentType)) {
            throw new BadRequestException(String.format("Invalid file format: %s. Only MP3 files are allowed", contentType));
        }

        Resource resource = new Resource(body);
        repository.save(resource);

        SongDto songDto = mp3ParserService.readSongMetadata(body);
        songDto.setId(resource.getId());
        int songId = songIntegrationService.addSong(songDto);

        if (songId != resource.getId()) {
            throw new InternalServerErrorException("Error occurred when creating song");
        }

        return resource.getId();
    }

    public byte[] getResourceById(String id) {
        int validatedId = validator.validateId(id);
        var song = repository.findById(validatedId);

        if (song.isPresent()) {
            return song.get().getResourceData();
        }

        throw new ResourceNotFoundException(String.format("Resource with ID=%d not found", validatedId));
    }

    public List<Integer> deleteResourceById(String ids) {
        var validatedIds = validator.validateIds(ids);

        List<Resource> resources = repository.findAllById(validatedIds);
        var existingResourceIds = resources.stream().map(Resource::getId).toList();

        var songServiceDeletedIds = songIntegrationService.deleteSongById(validatedIds);

        var filteredIds = existingResourceIds.stream()
                .filter(songServiceDeletedIds::contains)
                .toList();

        repository.deleteAllByIdInBatch(filteredIds);
        return filteredIds;
    }
}
