package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.model.Weather;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.WeatherRepository;
import at.fhtw.sampleapp.persistence.repository.WeatherRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

public class WeatherService extends AbstractService {

    private WeatherRepository weatherRepository;

    public WeatherService() {
        weatherRepository = new WeatherRepositoryImpl(new UnitOfWork());
    }

    // GET /weather(:id
    public Response getWeather(String id)
    {
        System.out.println("get weather for id: " + id);
        Weather weather = weatherRepository.findById(Integer.parseInt(id));
        String json = null;
        try {
            json = this.getObjectMapper().writeValueAsString(weather);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new Response(HttpStatus.OK, ContentType.JSON, json);
    }
    // GET /weather
    public Response getWeather() {
        return new Response(HttpStatus.NOT_IMPLEMENTED);
    }

    // POST /weather
    public Response addWeather(Request request) {
        return new Response(HttpStatus.NOT_IMPLEMENTED);
    }

    // GET /weather
    public Response getWeatherPerRepository() {
        System.out.println("getWeatherPerRepository");
        Weather weather = new Weather(1, "Austria", "Vienna", 22);
        String json = null;
        try {
            json = this.getObjectMapper().writeValueAsString(weather);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new Response(HttpStatus.OK, ContentType.JSON, json);
    }
}
