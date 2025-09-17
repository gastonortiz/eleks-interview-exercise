package org.eleks.interview.exercise.controller.converter;

import lombok.extern.slf4j.Slf4j;
import org.eleks.interview.exercise.error.api.InvalidCityCodeException;
import org.eleks.interview.exercise.model.CityCode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class CityCodeConverter implements Converter<String, CityCode> {

    private final Map<String, CityCode> cache;

    public CityCodeConverter(Map<String, CityCode> cache) {
        log.debug("Creating CityCodeConverter with cache size: {}", cache.size());
        this.cache = cache;
    }

    @Override
    public CityCode convert(@Nullable String code) {
        log.debug("Converting city code: {}", code);
        CityCode result = Optional.ofNullable(code).map(String::toUpperCase).map(cache::get).orElseThrow(() -> new InvalidCityCodeException(code));
        log.debug("Converted city code: {}", result);
        return result;
    }
}
