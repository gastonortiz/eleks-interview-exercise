package org.eleks.interview.exercise.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eleks.interview.exercise.controller.converter.CityCodeConverter;
import org.eleks.interview.exercise.model.CityCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.csv.CSVFormat.Builder.create;

@Slf4j
@Configuration
public class HttpConfig {
    @Value("${org.eleks.interview.exercise.city.codes.path:city_codes.csv}")
    private String cityCodesPath;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Bean
    public Converter<String, CityCode> conversionService() throws IOException{
        log.info("Creating CityCode converter.");
        log.info("Using city codes file: {}", cityCodesPath);
        try (CSVParser parser = new CSVParser(new InputStreamReader(Optional.ofNullable(HttpConfig.class.getClassLoader().getResourceAsStream(cityCodesPath)).orElseThrow(FileNotFoundException::new)), create().setHeader().setSkipHeaderRecord(true).build())) {
            return new CityCodeConverter(parser.stream()
                    .map(CSVRecord::toMap)
                    .map(CityCode::new)
                    .collect(toMap(CityCode::code, identity())));
        }
    }
}
