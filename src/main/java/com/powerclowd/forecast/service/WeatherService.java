package com.powerclowd.forecast.service;

import com.powerclowd.forecast.model.data.*;
import com.powerclowd.forecast.persistence.entity.Weather;
import com.powerclowd.forecast.persistence.entity.WeatherForecast;
import com.powerclowd.forecast.persistence.repository.WeatherForecastRepository;
import com.powerclowd.forecast.persistence.repository.WeatherRepository;
import com.powerclowd.forecast.utils.DataComposer;
import com.powerclowd.forecast.components.RestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static com.powerclowd.forecast.configuration.CacheConfig.*;
import static java.util.Objects.isNull;
import static com.powerclowd.forecast.model.external.OWWeather.OWDailyWeatherForecast;
import static com.powerclowd.forecast.model.external.OWWeather.OWCurrentWeather;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;


@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestClient restClient;
    private final WeatherRepository weatherRepository;
    private final WeatherForecastRepository weatherForecastRepository;
    private final OpenWeatherService openWeatherService;

    @Value("${weather.expire.time.sec}")
    private Integer weatherExpireTimeSec;

    @Value("${forecast.expire.time.sec}")
    private Integer forecastExpireTimeSec;


    @Cacheable(value = CURRENT_WEATHER_CACHE, cacheManager = COMPOSITE_CACHE_MANAGER)
    public Weather getCurrentWeather(String latitude, String longitude) {

        var currentWeather = weatherRepository
                .findByLatitudeAndLongitude(latitude, longitude);

        if (idNeedRefreshWeather(currentWeather)) {
            return refreshWeatherData(latitude, longitude).getCurrentWeather();
        } else
            return currentWeather;
    }


    @Cacheable(value = FORECAST_WEATHER_CACHE, cacheManager = COMPOSITE_CACHE_MANAGER)
    public List<WeatherForecast> getWeatherForecast(String latitude, String longitude) {

        var currentDate = LocalDate.now().toEpochDay();
        var forecasts = weatherForecastRepository
                .findTop8AllByLatitudeAndLongitudeAndDateIsGreaterThanEqualOrderByDate(latitude, longitude, currentDate);

        if (idNeedRefreshForecast(forecasts)) {
            return refreshWeatherData(latitude, longitude).getForecasts();
        } else {
            return forecasts;
        }
    }


    @Transactional
    public WeatherFullInfo refreshWeatherData(String latitude, String longitude) {

        var response = openWeatherService.getWeather(latitude, longitude);

        var currentWeather = updateCurrentWeather(latitude, longitude, response.getCurrentWeather());
        var forecasts = updateWeatherForecasts(latitude, longitude, response.getWeatherForecasts());

        return WeatherFullInfo.builder()
                .currentWeather(currentWeather)
                .forecasts(forecasts)
                .build();
    }


    private Weather updateCurrentWeather(String latitude, String longitude, OWCurrentWeather owCurrentWeather) {

        var currentWeather = weatherRepository
                .findByLatitudeAndLongitude(latitude, longitude);

        if (isNull(currentWeather)) {
            currentWeather = DataComposer.buildWeatherEntity(latitude, longitude, owCurrentWeather);
        } else {
            currentWeather.setTemperature(owCurrentWeather.getTemperature());
            currentWeather.setFeelsLike(owCurrentWeather.getFeelsLike());
            currentWeather.setPressure(owCurrentWeather.getPressure());
            currentWeather.setHumidity(owCurrentWeather.getHumidity());
        }

        return weatherRepository.save(currentWeather);
    }


    private List<WeatherForecast> updateWeatherForecasts(String latitude, String longitude, List<OWDailyWeatherForecast> forecasts) {

        if (isEmpty(forecasts)) {
            return null;
        }

        var forecastStartDate = forecasts.get(0).getDate();

        var existingForecastsIds = weatherForecastRepository
                .findTop8AllByLatitudeAndLongitudeAndDateIsGreaterThanEqualOrderByDate(latitude, longitude, forecastStartDate)
                .stream()
                .map(WeatherForecast::getId)
                .collect(toList());

        var newForecasts = DataComposer.buildWeatherForecastEntities(latitude, longitude, forecasts);

        weatherForecastRepository.deleteByIdIn(existingForecastsIds);

        return weatherForecastRepository.saveAll(newForecasts);
    }


    private boolean idNeedRefreshWeather(Weather currentWeather) {

        var currentTime = Instant.now().getEpochSecond();

        return isNull(currentWeather) ||
                currentTime - currentWeather.getUpdatedAt().getEpochSecond() >= weatherExpireTimeSec;
    }


    private boolean idNeedRefreshForecast(List<WeatherForecast> forecasts) {

        var currentTime = Instant.now().getEpochSecond();

        return isEmpty(forecasts)        // no forecast yet
                || forecasts.size() < 8  // not enough forecast
                || currentTime - forecasts.get(0).getUpdatedAt().getEpochSecond() >= forecastExpireTimeSec; // expired forecast
    }

}
