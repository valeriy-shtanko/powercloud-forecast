package com.powerclowd.forecast.utils;

import com.powerclowd.forecast.model.resources.WeatherDailyForecastResource;
import com.powerclowd.forecast.model.resources.WeatherForecastResource;
import com.powerclowd.forecast.model.resources.WeatherResource;
import com.powerclowd.forecast.persistence.entity.Weather;
import com.powerclowd.forecast.persistence.entity.WeatherForecast;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import static com.powerclowd.forecast.model.external.OWWeather.OWDailyWeatherForecast;
import static com.powerclowd.forecast.model.external.OWWeather.OWCurrentWeather;


@UtilityClass
public class DataComposer {


    public static Weather buildWeatherEntity(String latitude,
                                             String longitude,
                                             OWCurrentWeather owCurrentWeather) {

        return isNull(owCurrentWeather)
                ? null
                : Weather.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .temperature(owCurrentWeather.getTemperature())
                    .feelsLike(owCurrentWeather.getFeelsLike())
                    .pressure(owCurrentWeather.getPressure())
                    .humidity(owCurrentWeather.getHumidity())
                    .build();
    }


    public static WeatherForecast buildWeatherForecastEntity(String latitude,
                                                             String longitude,
                                                             OWDailyWeatherForecast owDailyWeatherForecast) {

        return isNull(owDailyWeatherForecast)
                ? null
                : WeatherForecast.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .date(owDailyWeatherForecast.getDate())
                    .humidity(owDailyWeatherForecast.getHumidity())
                    .pressure(owDailyWeatherForecast.getPressure())
                    .minTemperature(owDailyWeatherForecast.getTemperature().getMinTemperature())
                    .maxTemperature(owDailyWeatherForecast.getTemperature().getMaxTemperature())
                    .build();
    }



    public static List<WeatherForecast> buildWeatherForecastEntities(String latitude,
                                                                     String longitude,
                                                                     List<OWDailyWeatherForecast> forecasts) {

        return forecasts.stream()
                .filter(Objects::nonNull)
                .map(f -> buildWeatherForecastEntity(latitude, longitude, f))
                .collect(toList());
    }


    public static WeatherResource buildWeatherResource(Weather weather) {

        return isNull(weather)
                ? null
                : WeatherResource.builder()
                    .latitude(weather.getLatitude())
                    .longitude(weather.getLongitude())
                    .pressure(weather.getPressure())
                    .temperature(weather.getTemperature())
                    .humidity(weather.getHumidity())
                    .feelsLike(weather.getFeelsLike())
                    .build();
    }


    public static WeatherDailyForecastResource buildWeatherDailyForecastResource(WeatherForecast forecast) {

        return isNull(forecast)
                ? null
                : WeatherDailyForecastResource.builder()
                    .date(forecast.getDate())
                    .humidity(forecast.getHumidity())
                    .maxTemperature(forecast.getMaxTemperature())
                    .minTemperature(forecast.getMinTemperature())
                    .pressure(forecast.getPressure())
                    .build();
    }


    public static WeatherForecastResource buildWeatherForecastResource(String latitude,
                                                                       String longitude,
                                                                       List<WeatherForecast> forecast) {
        var forecastResources = forecast.stream()
                .filter(Objects::nonNull)
                .map(DataComposer::buildWeatherDailyForecastResource)
                .collect(toList());

        return WeatherForecastResource.builder()
                .latitude(latitude)
                .longitude(longitude)
                .forecasts(forecastResources)
                .build();
    }
}
