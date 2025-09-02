package com.microservice.resource_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "resource")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "resource_location")
    private String resourceLocation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Resource(String resourceLocation) {
        this.resourceLocation = resourceLocation;
        this.createdAt = LocalDateTime.now();
    }
}
