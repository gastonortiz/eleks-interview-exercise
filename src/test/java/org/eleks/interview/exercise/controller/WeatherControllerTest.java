package org.eleks.interview.exercise.controller;

import org.eleks.interview.exercise.model.CityCode;
import org.eleks.interview.exercise.model.Report;
import org.eleks.interview.exercise.model.Weather;
import org.eleks.interview.exercise.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WeatherControllerTest {

    @Test
    public void getWeatherByCity_delegatesToService_andReturnsSameObject() {
        WeatherService weatherService = mock(WeatherService.class);
        WeatherController controller = new WeatherController(weatherService);

        // Assuming CityCode has a ctor like CityCode(String code, String name)
        CityCode city = new CityCode("BUE", "Buenos Aires");

        // Assuming Weather has fields like (double temp, String description, int humidity)
        Weather expected = new Weather(city, LocalDateTime.now(), new Report(19.4, "Sunny", 73));

        when(weatherService.getWeather(city)).thenReturn(expected);

        Weather actual = controller.getWeatherByCity(city);

        assertSame(expected, actual);
        verify(weatherService).getWeather(city);
        verifyNoMoreInteractions(weatherService);
    }
}
