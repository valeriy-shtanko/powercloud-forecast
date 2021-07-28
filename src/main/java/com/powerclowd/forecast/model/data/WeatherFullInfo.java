package com.powerclowd.forecast.model.data;


import com.powerclowd.forecast.persistence.entity.Weather;
import com.powerclowd.forecast.persistence.entity.WeatherForecast;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherFullInfo {

    private Weather currentWeather;
    private List<WeatherForecast> forecasts;
}
