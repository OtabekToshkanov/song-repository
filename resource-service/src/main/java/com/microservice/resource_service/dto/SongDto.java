package com.microservice.resource_service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class SongDto {
    private int id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
}
