package com.powerclowd.forecast.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

import static java.time.Instant.now;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "weather",
       indexes = @Index(name = "latitude_longitude_idx", columnList = "latitude, longitude"))
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "feels_like")
    private Double feelsLike;

    @Column(name = "pressure")
    private Integer pressure;

    @Column(name = "humidity")
    private Integer humidity;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void onPrePersist() {
        var now = now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onPreUpdate() {
        updatedAt = now();
    }
}
