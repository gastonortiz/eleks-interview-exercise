package org.eleks.interview.exercise.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.eleks.interview.exercise.error.api.CodedException;
import org.eleks.interview.exercise.error.internal.HttpInvocationException;
import org.eleks.interview.exercise.model.ApplicationError;
import org.eleks.interview.exercise.model.CityCode;
import org.eleks.interview.exercise.model.Report;
import org.eleks.interview.exercise.model.Weather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class WeatherApiServiceImpl extends HttpClientService implements WeatherService {

    public WeatherApiServiceImpl(@Value("${org.eleks.interview.exercise.weather.api.url}") String baseUrl,
                                 @Value("${org.eleks.interview.exercise.weather.api.key}") String apiKey,
                                 OkHttpClient okHttpClient,
                                 ObjectMapper objectMapper) {
        super(baseUrl, okHttpClient, objectMapper, Map.of("key", apiKey));
    }

    @Override
    public Weather getWeather(CityCode cityCode) {
        try {
            return new Weather(cityCode, LocalDateTime.now(), super.get("current.json", Map.of("q", cityCode.name()), Report.class, ApplicationError.class));
        } catch (HttpInvocationException e) {
            throw new CodedException(e.getParsedResponse(), e);
        }
    }
}
