package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.Weather;

import java.util.Collection;

public interface WeatherRepository {

    Weather findById(int id);
    Collection<Weather> findAllWeather();

}
