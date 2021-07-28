package com.powerclowd.forecast.model.resources;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WeatherResource {

    private String latitude;
    private String longitude;

    private Double temperature;
    private Double feelsLike;
    private Integer pressure;
    private Integer humidity;
}
