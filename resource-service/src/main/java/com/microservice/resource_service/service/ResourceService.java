package com.microservice.resource_service.service;

import com.microservice.resource_service.excpetion.BadRequestException;
import com.microservice.resource_service.excpetion.InternalServerErrorException;
import com.microservice.resource_service.excpetion.ResourceNotFoundException;
import com.microservice.resource_service.model.Resource;
import com.microservice.resource_service.repository.ResourceRepository;
import com.microservice.resource_service.util.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final Validator validator;
    private final ResourceRepository repository;
    private final S3Service s3Service;
    private final RabbitMQService rabbitMQService;
    private final SongIntegrationService songIntegrationService;

    @Transactional
    public Integer createResource(String contentType, byte[] body) {
        if (!"audio/mpeg".equals(contentType)) {
            throw new BadRequestException(String.format("Invalid file format: %s. Only MP3 files are allowed", contentType));
        }

        var resourceLocation = s3Service.uploadFile(body, contentType);

        Resource resource = new Resource(resourceLocation);
        repository.save(resource);

        rabbitMQService.sendMessageAsync(resource.getId());

        return resource.getId();
    }

    public byte[] getResourceById(String id) {
        int validatedId = validator.validateId(id);
        var song = repository.findById(validatedId);

        if (song.isPresent()) {
            return s3Service.downloadFile(song.get().getResourceLocation());
        }

        throw new ResourceNotFoundException(String.format("Resource with ID=%d not found", validatedId));
    }

    public List<Integer> deleteResourceById(String ids) {
        var validatedIds = validator.validateIds(ids);

        List<Resource> resources = repository.findAllById(validatedIds);
        LinkedList<Integer> filteredIds = new LinkedList<>();

        resources.forEach(resource -> {
            try {
                s3Service.deleteFile(resource.getResourceLocation());
                filteredIds.add(resource.getId());
            } catch (InternalServerErrorException ignored) {
            }
        });

        songIntegrationService.deleteSongById(filteredIds);

        repository.deleteAllByIdInBatch(filteredIds);
        return filteredIds;
    }
}
