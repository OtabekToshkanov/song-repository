package com.microservice.song_service.service;

import com.microservice.song_service.dto.SongDto;
import com.microservice.song_service.exception.ConflictException;
import com.microservice.song_service.exception.SongNotFoundException;
import com.microservice.song_service.model.Song;
import com.microservice.song_service.repository.SongRepository;
import com.microservice.song_service.util.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {
    private final Validator validator;
    private final SongRepository repository;

    public Integer createSong(SongDto songDto) {
        Song song = songDto.createSong();

        if (repository.existsById(song.getId())) {
            throw new ConflictException(String.format("Metadata for resource ID=%d already exists", song.getId()));
        }

        return repository.save(song).getId();
    }

    public SongDto getSongById(String id) {
        var validatedId = validator.validateId(id);
        var song = repository.findById(validatedId);

        if (song.isPresent()) {
            return SongDto.toSongDto(song.get());
        }

        throw new SongNotFoundException(String.format("Song metadata for ID=%d not found", validatedId));
    }

    public List<Integer> deleteSongById(String ids) {
        var validatedIds = validator.validateIds(ids);
        List<Song> resources = repository.findAllById(validatedIds);
        var existingSongIds = resources.stream().map(Song::getId).toList();
        repository.deleteAllByIdInBatch(existingSongIds);
        return existingSongIds;
    }
}
