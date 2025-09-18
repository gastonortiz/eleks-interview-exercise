package org.eleks.interview.exercise.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.eleks.interview.exercise.error.internal.HttpConnectionException;
import org.eleks.interview.exercise.error.internal.HttpInvocationException;
import org.eleks.interview.exercise.error.internal.ResponseParsingException;
import org.eleks.interview.exercise.error.internal.MissingResponseException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

@Slf4j
public class HttpClientService {

    private final OkHttpClient okHttpClient;
    private final String baseUrl;
    private final Map<String, String> defaultQueryParams;
    private final ObjectMapper objectMapper;

    public HttpClientService(String baseUrl,
                             OkHttpClient okHttpClient,
                             ObjectMapper objectMapper,
                             Map<String, String> defaultQueryParams) {
        this.okHttpClient = okHttpClient;
        this.baseUrl = baseUrl;
        this.defaultQueryParams = defaultQueryParams;
        this.objectMapper = objectMapper;
    }

    protected <RETURN_TYPE, ERROR_TYPE> RETURN_TYPE get(String relativePath, Map<String, String> queryParams, Class<RETURN_TYPE> returnTypeClass, Class<ERROR_TYPE> errorTypeClass) {
        try (Response response = okHttpClient.newCall(new okhttp3.Request.Builder()
                        .url(concat(defaultQueryParams.entrySet().stream(), queryParams.entrySet().stream())
                                .map(entry -> "%s=%s".formatted(entry.getKey(), encode(entry.getValue(), UTF_8)))
                                .collect(joining("&", "%s/%s?".formatted(baseUrl, relativePath), "")))
                        .build())
                .execute()) {
            if (response.isSuccessful()) {
                return parseResponse(response, returnTypeClass);
            } else {
                throw new HttpInvocationException(parseResponse(response, errorTypeClass), "%s/%s".formatted(baseUrl, relativePath), response.code());
            }
        } catch (IOException e) {
            throw new HttpConnectionException(e);
        }
    }

    private <T> T parseResponse(Response response, Class<T> parsedClass) {
        return Optional.of(response)
                .map(Response::body)
                .map(responseBody -> {
                    try {
                        String body = responseBody.string();
                        log.trace(body);
                        return objectMapper.readValue(body, parsedClass);
                    } catch (IOException e) {
                        throw new ResponseParsingException(e);
                    }
                })
                .orElseThrow(MissingResponseException::new);
    }
}
