package com.powerclowd.forecast.model.external;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OWWeather {

    @JsonProperty("current")
    OWCurrentWeather currentWeather;

    @JsonProperty("daily")
    List<OWDailyWeatherForecast> weatherForecasts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OWCurrentWeather {

        @JsonProperty("temp")
        private Double temperature;

        @JsonProperty("feels_like")
        private Double feelsLike;

        private Integer pressure;

        private Integer humidity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OWDailyWeatherForecast {

        @JsonProperty("dt")
        private Long date;

        private Integer pressure;

        private Integer humidity;

        @JsonProperty("temp")
        private OWDailyTemperatureForecast temperature;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OWDailyTemperatureForecast {

        @JsonProperty("min")
        private Double minTemperature;

        @JsonProperty("max")
        private Double maxTemperature;
    }
}
