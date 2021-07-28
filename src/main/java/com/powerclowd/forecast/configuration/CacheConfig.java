package com.powerclowd.forecast.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.*;


@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig extends CachingConfigurerSupport {

    @Value("${weather.expire.time.sec}")
    private Integer weatherExpireTimeSec;

    @Value("${forecast.expire.time.sec}")
    private Integer forecastExpireTimeSec;


    public static final String COMPOSITE_CACHE_MANAGER = "compositeCacheManager";
    public static final String CURRENT_WEATHER_CACHE = "currentWeatherCache";
    public static final String FORECAST_WEATHER_CACHE = "forecastWeatherCache";


    @Primary
    @Bean
    public CacheManager compositeCacheManager() {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();

        compositeCacheManager.setCacheManagers(List.of(
                createCache(CURRENT_WEATHER_CACHE, 10_000, weatherExpireTimeSec),
                createCache(FORECAST_WEATHER_CACHE, 10_000, forecastExpireTimeSec)
        ));

        return compositeCacheManager;
    }

    private CacheManager createCache(String cacheName, Integer maximumSize, Integer expireTimeSec) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireTimeSec, SECONDS);

        cacheManager.setCaffeine(cacheBuilder);
        cacheManager.setCacheNames(Set.of(cacheName));

        return cacheManager;
    }
}
