package com.microservice.song_service.controller;

import com.microservice.song_service.dto.SongDto;
import com.microservice.song_service.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class SongController {
    private final SongService songService;

    @PostMapping("/songs")
    public ResponseEntity<Map<String, Integer>> addSong(@Valid @RequestBody SongDto songDto) {
        var songId = songService.createSong(songDto);
        return ResponseEntity.ok(Map.of("id", songId));
    }

    @GetMapping("/songs/{id}")
    public ResponseEntity<SongDto> getSongById(@PathVariable String id) {
        var songDto = songService.getSongById(id);
        return ResponseEntity.ok(songDto);
    }

    @DeleteMapping("/songs")
    public ResponseEntity<Map<String, List<Integer>>> deleteSongById(@RequestParam("id") String ids) {
        List<Integer> deletedSongIds = songService.deleteSongById(ids);
        return ResponseEntity.ok(Map.of("ids", deletedSongIds));
    }
}
