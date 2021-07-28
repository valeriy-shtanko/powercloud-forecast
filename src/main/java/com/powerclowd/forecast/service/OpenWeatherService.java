package com.powerclowd.forecast.service;

import com.powerclowd.forecast.components.RestClient;
import com.powerclowd.forecast.model.external.OWWeather;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@RequiredArgsConstructor
public class OpenWeatherService {

    private final RestClient restClient;

    @Value("${open.weather.url}")
    private String openWeatherApiUrl;

    @Value("${open.weather.api.key}")
    private String openWeatherApiKey;


    public OWWeather getWeather(String latitude, String longitude) {

        String requestUrl = UriComponentsBuilder
                .fromHttpUrl(openWeatherApiUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("exclude", "hourly,minutely")
                .queryParam("units", "metric")
                .queryParam("appid", openWeatherApiKey)
                .toUriString();

        return restClient.getObject(requestUrl, OWWeather.class);
    }
}
