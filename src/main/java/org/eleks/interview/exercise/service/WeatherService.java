package org.eleks.interview.exercise.service;

import org.eleks.interview.exercise.model.CityCode;
import org.eleks.interview.exercise.model.Weather;

public interface WeatherService {
    Weather getWeather(CityCode cityCode);
}
