package com.microservice.resource_processor.controller;

import com.microservice.resource_processor.dto.SongDto;
import com.microservice.resource_processor.service.Mp3ParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class ResourceController {
    private final Mp3ParserService mp3ParserService;

    @PostMapping("/resources")
    public ResponseEntity<SongDto> createResource(@RequestBody byte[] body) {
        return ResponseEntity.ok(mp3ParserService.readSongMetadata(body));
    }
}
