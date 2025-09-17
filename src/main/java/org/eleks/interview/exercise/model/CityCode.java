package org.eleks.interview.exercise.model;

import java.util.Map;

/**
 * Represents a city code and its name.
 *
 * @param code The IATA city code.
 * @param name The name of the city.
 */
public record CityCode(String code, String name) {
    public CityCode(Map<String, String> map) {
        this(map.get("city_code"), map.get("name"));
    }

    public String toString() {
        return "%s (%s)".formatted(code, name);
    }
}
