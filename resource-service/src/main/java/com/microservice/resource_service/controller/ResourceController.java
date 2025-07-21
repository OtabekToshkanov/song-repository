package com.microservice.resource_service.controller;

import com.microservice.resource_service.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class ResourceController {
    private final ResourceService service;

    @PostMapping("/resources")
    public ResponseEntity<Map<String, Integer>> createResource(@RequestHeader("Content-Type") String contentType, @RequestBody byte[] body) {
        int songId = service.createResource(contentType, body);
        return ResponseEntity.ok(Map.of("id", songId));
    }

    @GetMapping(value = "/resources/{id}")
    public ResponseEntity<byte[]> getResourceById(@PathVariable String id) {
        var resource = service.getResourceById(id);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "audio/mpeg");
        responseHeaders.add("Content-Length", String.valueOf(resource.length));

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(resource);
    }

    @DeleteMapping("/resources")
    public ResponseEntity<Map<String, List<Integer>>> deleteResourcesById(@RequestParam("id") String ids) {
        List<Integer> deletedSongIds = service.deleteResourceById(ids);
        return ResponseEntity.ok(Map.of("ids", deletedSongIds));
    }
}
