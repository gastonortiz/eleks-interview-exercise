package org.eleks.interview.exercise.model;

import java.time.LocalDateTime;

public record Weather(CityCode cityCode, LocalDateTime timestamp, Report report) {
}
