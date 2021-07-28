package com.powerclowd.forecast.persistence.repository;

import com.powerclowd.forecast.persistence.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Integer>  {

    Weather findByLatitudeAndLongitude(String latitude, String longitude);
}
