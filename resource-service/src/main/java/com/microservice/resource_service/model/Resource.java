package com.microservice.resource_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "resource_data", columnDefinition = "bytea")
    private byte[] resourceData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Resource(byte[] resourceData) {
        this.resourceData = resourceData;
        this.createdAt = LocalDateTime.now();
    }
}
