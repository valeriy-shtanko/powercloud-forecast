package com.powerclowd.forecast.api;

import com.powerclowd.forecast.ForecastApplication;
import com.powerclowd.forecast.components.RestClient;
import com.powerclowd.forecast.configuration.CacheConfig;
import com.powerclowd.forecast.model.external.OWWeather;

import com.powerclowd.forecast.model.resources.WeatherDailyForecastResource;
import com.powerclowd.forecast.model.resources.WeatherForecastResource;
import com.powerclowd.forecast.model.resources.WeatherResource;
import com.powerclowd.forecast.persistence.repository.WeatherForecastRepository;
import com.powerclowd.forecast.persistence.repository.WeatherRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.specification.RequestSpecification;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;

import static com.powerclowd.forecast.model.external.OWWeather.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ForecastApplication.class })
public class WeatherControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private RestClient restClient;

    @Autowired
    WeatherRepository weatherRepository;
    @Autowired
    WeatherForecastRepository weatherForecastRepository;
    @Autowired
    CacheManager cacheManager;

    private static final String LATITUDE = "33.44";
    private static final String LONGITUDE = "-94.04";
    private static final Long DAY_IN_SECS = 86400L;


    @Before
    public void before() {
        Mockito.reset(restClient);

        weatherRepository.deleteAll();
        weatherForecastRepository.deleteAll();

        cacheManager.getCache(CacheConfig.CURRENT_WEATHER_CACHE).clear();
        cacheManager.getCache(CacheConfig.FORECAST_WEATHER_CACHE).clear();
    }


    @Test
    public void should_get_current_weather() {

         var mockOWResponse = getOWWeather();
         var expected = WeatherResource.builder()
                 .latitude(LATITUDE)
                 .longitude(LONGITUDE)
                 .feelsLike(mockOWResponse.getCurrentWeather().getFeelsLike())
                 .humidity(mockOWResponse.getCurrentWeather().getHumidity())
                 .temperature(mockOWResponse.getCurrentWeather().getTemperature())
                 .pressure(mockOWResponse.getCurrentWeather().getPressure())
                 .build();

        doReturn(mockOWResponse)
                .when(restClient)
                .getObject(anyString(), any());

        // Should call external service for data
        var actual = getCurrentWeather();

        verify(restClient, times(1))
                .getObject(anyString(), any());

        assertThat(actual, is(expected));

        // Should not call external service but get data from DB
        getCurrentWeather();

        verify(restClient, times(1))
                .getObject(anyString(), any());
    }


    @Test
    public void should_get_weather_forecast() {

        var mockOWResponse = getOWWeather();
        var expected = getWeatherForecastResource();

        doReturn(mockOWResponse)
                .when(restClient)
                .getObject(anyString(), any());

        // Should call external service for data
        var actual = getWeatherForecast();

        verify(restClient, times(1))
                .getObject(anyString(), any());

        assertThat(actual, is(expected));

        // Should not call external service but get data from DB
        getCurrentWeather();

        verify(restClient, times(1))
                .getObject(anyString(), any());
    }


    private RequestSpecification request() {
        return given()
                .baseUri("http://localhost/api/weather")
                .port(port);
    }


    private WeatherResource getCurrentWeather() {

        return request()
                .basePath("/current")
                .queryParam("latitude", LATITUDE)
                .queryParam("longitude", LONGITUDE)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body()
                .as(WeatherResource.class);
    }


    private WeatherForecastResource getWeatherForecast() {

        return request()
                .basePath("/forecast")
                .queryParam("latitude", LATITUDE)
                .queryParam("longitude", LONGITUDE)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body()
                .as(WeatherForecastResource.class);
    }


    private static OWWeather getOWWeather() {

        var owCurrentWeather = OWCurrentWeather.builder()
                .feelsLike(1000.)
                .temperature(2000.)
                .humidity(3000)
                .pressure(4000)
                .build();

        var currentDate = LocalDate.now().toEpochDay();

        var forecasts = List.of(
                new OWDailyWeatherForecast(currentDate + DAY_IN_SECS, 2000, 3000, new OWDailyTemperatureForecast(400., 500.)),
                new OWDailyWeatherForecast(currentDate + 2 * DAY_IN_SECS, 2001, 3001, new OWDailyTemperatureForecast(401., 501.)),
                new OWDailyWeatherForecast(currentDate + 3 * DAY_IN_SECS, 2002, 3002, new OWDailyTemperatureForecast(402., 502.)),
                new OWDailyWeatherForecast(currentDate + 4 * DAY_IN_SECS, 2003, 3003, new OWDailyTemperatureForecast(403., 503.)),
                new OWDailyWeatherForecast(currentDate + 5 * DAY_IN_SECS, 2004, 3004, new OWDailyTemperatureForecast(404., 504.)),
                new OWDailyWeatherForecast(currentDate + 6 * DAY_IN_SECS, 2005, 3005, new OWDailyTemperatureForecast(405., 505.)),
                new OWDailyWeatherForecast(currentDate + 7 * DAY_IN_SECS, 2006, 3006, new OWDailyTemperatureForecast(406., 506.)),
                new OWDailyWeatherForecast(currentDate + 8 * DAY_IN_SECS, 2007, 3007, new OWDailyTemperatureForecast(407., 507.))
        );

        return OWWeather.builder()
                .currentWeather(owCurrentWeather)
                .weatherForecasts(forecasts)
                .build();
    }


    private static WeatherForecastResource getWeatherForecastResource() {

        var currentDate = LocalDate.now().toEpochDay();

        var forecasts = List.of(
                new WeatherDailyForecastResource(currentDate +     DAY_IN_SECS, 2000, 3000, 400., 500.),
                new WeatherDailyForecastResource(currentDate + 2 * DAY_IN_SECS, 2001, 3001, 401., 501.),
                new WeatherDailyForecastResource(currentDate + 3 * DAY_IN_SECS, 2002, 3002, 402., 502.),
                new WeatherDailyForecastResource(currentDate + 4 * DAY_IN_SECS, 2003, 3003, 403., 503.),
                new WeatherDailyForecastResource(currentDate + 5 * DAY_IN_SECS, 2004, 3004, 404., 504.),
                new WeatherDailyForecastResource(currentDate + 6 * DAY_IN_SECS, 2005, 3005, 405., 505.),
                new WeatherDailyForecastResource(currentDate + 7 * DAY_IN_SECS, 2006, 3006, 406., 506.),
                new WeatherDailyForecastResource(currentDate + 8 * DAY_IN_SECS, 2007, 3007, 407., 507.)
        );

        return WeatherForecastResource.builder()
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .forecasts(forecasts)
                .build();


    }
}
