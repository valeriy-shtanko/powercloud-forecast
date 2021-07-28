package com.powerclowd.forecast.model.resources;

import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WeatherForecastResource {

    private String latitude;
    private String longitude;

    private List<WeatherDailyForecastResource> forecasts;
}
