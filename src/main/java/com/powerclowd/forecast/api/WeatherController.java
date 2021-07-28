package com.powerclowd.forecast.api;

import com.powerclowd.forecast.model.resources.WeatherForecastResource;
import com.powerclowd.forecast.model.resources.WeatherResource;
import com.powerclowd.forecast.service.WeatherService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.powerclowd.forecast.utils.DataComposer.buildWeatherForecastResource;
import static com.powerclowd.forecast.utils.DataComposer.buildWeatherResource;


@RestController
@RequestMapping("api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping(value = "/current")
    public WeatherResource getCurrentWeather(@RequestParam(value = "latitude")  String latitude,
                                             @RequestParam(value = "longitude") String longitude) {

        var weather =  weatherService.getCurrentWeather(latitude, longitude);

        return buildWeatherResource(weather);
    }


    @GetMapping(value = "/forecast")
    public WeatherForecastResource getWeatherForecast(@RequestParam(value = "latitude")  String latitude,
                                                      @RequestParam(value = "longitude") String longitude) {

        var forecasts =  weatherService.getWeatherForecast(latitude, longitude);

        return buildWeatherForecastResource(latitude, longitude, forecasts);
    }
}
