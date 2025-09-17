package org.eleks.interview.exercise.controller.converter;

import org.eleks.interview.exercise.error.api.InvalidCityCodeException;
import org.eleks.interview.exercise.model.CityCode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CityCodeConverterTest {

    private static CityCode city(String code, String name) {
        // Adjust keys if your CityCode expects different map keys
        return new CityCode(Map.of(
                "code", code,
                "name", name
        ));
    }

    @Test
    public void convertsKnownCode_exactMatch() {
        CityCode bue = city("BUE", "Buenos Aires");
        CityCodeConverter converter = new CityCodeConverter(Map.of(
                "BUE", bue
        ));

        CityCode result = converter.convert("BUE");

        assertSame(bue, result, "Should return the exact cached CityCode instance");
    }

    @Test
    public void convertsKnownCode_caseInsensitive() {
        CityCode bue = city("BUE", "Buenos Aires");
        CityCodeConverter converter = new CityCodeConverter(Map.of(
                "BUE", bue
        ));

        CityCode resultLower = converter.convert("bue");
        CityCode resultMixed = converter.convert("BuE");

        assertSame(bue, resultLower);
        assertSame(bue, resultMixed);
    }

    @Test
    public void throwsForUnknownCode() {
        CityCodeConverter converter = new CityCodeConverter(Map.of(
                "NYC", city("NYC", "New York")
        ));

        assertThrows(InvalidCityCodeException.class, () -> converter.convert("XXX"));
    }

    @Test
    public void throwsForNullCode() {
        CityCodeConverter converter = new CityCodeConverter(Map.of(
                "TYO", city("TYO", "Tokyo")
        ));

        assertThrows(InvalidCityCodeException.class, () -> converter.convert(null));
    }
}
