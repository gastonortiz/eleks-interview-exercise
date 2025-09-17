package org.eleks.interview.exercise.config;

import okhttp3.OkHttpClient;
import org.eleks.interview.exercise.model.CityCode;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class HttpConfigTest {

    @Test
    void okHttpClientBeanIsCreated() {
        HttpConfig cfg = new HttpConfig();
        OkHttpClient client = cfg.okHttpClient();
        assertNotNull(client);
    }

    @Test
    void conversionServiceLoadsCsvFromClasspathAndConvertsKnownCode() throws Exception {
        HttpConfig cfg = new HttpConfig();
        // point to the CSV in src/test/resources
        setField(cfg, "cityCodesPath", "city_codes.csv");

        Converter<String, CityCode> converter = cfg.conversionService();

        assertNotNull(converter, "converter bean must be created");

        CityCode bue = converter.convert("BUE");
        assertNotNull(bue, "known code should resolve to a CityCode");
        assertEquals("BUE", bue.code(), "code should match input");
    }

    @Test
    void conversionServiceThrowsWhenFileMissing() {
        HttpConfig cfg = new HttpConfig();
        setField(cfg, "cityCodesPath", "does-not-exist.csv");

        assertThrows(FileNotFoundException.class, cfg::conversionService,
                "missing resource should throw FileNotFoundException");
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
