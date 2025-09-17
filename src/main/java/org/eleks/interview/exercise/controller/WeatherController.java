package org.eleks.interview.exercise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.eleks.interview.exercise.model.ApplicationError;
import org.eleks.interview.exercise.model.CityCode;
import org.eleks.interview.exercise.model.Weather;
import org.eleks.interview.exercise.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Weather")
@Slf4j
@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get current weather by IATA city code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Weather.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ApplicationError.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApplicationError.class))),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content(schema = @Schema(implementation = ApplicationError.class)))
    })
    public Weather getWeatherByCity(
            @PathVariable("code") CityCode cityCode) {
        log.info("Getting weather for {}({})", cityCode.name(), cityCode.code());
        return weatherService.getWeather(cityCode);
    }
}
