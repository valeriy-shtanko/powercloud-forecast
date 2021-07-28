package com.powerclowd.forecast.utils;

import com.powerclowd.forecast.model.resources.WeatherDailyForecastResource;
import com.powerclowd.forecast.model.resources.WeatherResource;
import com.powerclowd.forecast.persistence.entity.Weather;
import com.powerclowd.forecast.persistence.entity.WeatherForecast;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class DataComposerTest {

    private static final String LATITUDE = "33.44";
    private static final String LONGITUDE = "-94.04";

    @Test
    public void should_build_weather_daily_forecast_resource() {

        var input = WeatherForecast.builder()
                .date(12345678L)
                .humidity(10001)
                .pressure(10002)
                .minTemperature(100.)
                .minTemperature(200.)
                .build();

        var expected = WeatherDailyForecastResource.builder()
                .date(input.getDate())
                .humidity(input.getHumidity())
                .maxTemperature(input.getMaxTemperature())
                .minTemperature(input.getMinTemperature())
                .pressure(input.getPressure())
                .build();

        var actual = DataComposer.buildWeatherDailyForecastResource(input);

        assertThat(actual, is(expected));
    }

    @Test
    public void should_return_null_weather_daily_forecast_resource() {

        var actual = DataComposer.buildWeatherDailyForecastResource(null);

        assertThat(actual, is(nullValue()));
    }


    @Test
    public void should_build_weather_resource() {

        var input = Weather.builder()
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .pressure(1000)
                .temperature(2000.)
                .humidity(3000)
                .feelsLike(4000.)
                .build();

        var expected = WeatherResource.builder()
                .latitude(input.getLatitude())
                .longitude(input.getLongitude())
                .pressure(input.getPressure())
                .temperature(input.getTemperature())
                .humidity(input.getHumidity())
                .feelsLike(input.getFeelsLike())
                .build();

        var actual = DataComposer.buildWeatherResource(input);

        assertThat(actual, is(expected));
    }

    @Test
    public void should_return_null_weather_resource() {

        var actual = DataComposer.buildWeatherResource(null);

        assertThat(actual, is(nullValue()));
    }
}
