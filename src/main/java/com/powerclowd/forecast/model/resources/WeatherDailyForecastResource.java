package com.powerclowd.forecast.model.resources;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WeatherDailyForecastResource {

    private Long date;
    private Integer pressure;
    private Integer humidity;
    private Double minTemperature;
    private Double maxTemperature;
}
