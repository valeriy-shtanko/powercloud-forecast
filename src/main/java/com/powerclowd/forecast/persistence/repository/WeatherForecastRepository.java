package com.powerclowd.forecast.persistence.repository;


import com.powerclowd.forecast.persistence.entity.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Collection;
import java.util.List;


public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Integer> {

    List<WeatherForecast> findTop8AllByLatitudeAndLongitudeAndDateIsGreaterThanEqualOrderByDate(String latitude, String longitude, Long Date);



    @Modifying
    void deleteByIdIn(Collection<Integer> id);
}
