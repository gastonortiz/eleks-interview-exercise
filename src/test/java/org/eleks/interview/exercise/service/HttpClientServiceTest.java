package org.eleks.interview.exercise.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.eleks.interview.exercise.error.internal.HttpConnectionException;
import org.eleks.interview.exercise.error.internal.HttpInvocationException;
import org.eleks.interview.exercise.error.internal.MissingResponseException;
import org.eleks.interview.exercise.error.internal.ResponseParsingException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for HttpClientService#get(...)
 */
public class HttpClientServiceTest {

    private final OkHttpClient http = mock(OkHttpClient.class);
    private final Call call = mock(Call.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl = "http://example.com/v1";

    // Simple DTOs for success/error payloads
    static record SuccessDto(String ok) {}
    static record ErrorDto(String message) {}

    @Test
    public void get_success_buildsUrl_mergesAndEncodesParams_andParsesBody() throws Exception {
        // given
        Map<String, String> defaults = Map.of("apikey", "api key");   // space -> '+'
        Map<String, String> q = Map.of("city", "BUE", "q", "a b");    // space -> '+'

        HttpClientService svc = new HttpClientService(baseUrl, http, mapper, defaults);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(http.newCall(requestCaptor.capture())).thenReturn(call);

        String body = "{\"ok\":\"yes\"}";
        when(call.execute()).thenAnswer(inv -> new Response.Builder()
                .request(requestCaptor.getValue())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(body, MediaType.parse("application/json")))
                .build());

        // when
        SuccessDto dto = svc.get("weather", q, SuccessDto.class, ErrorDto.class);

        // then parsed
        assertNotNull(dto);
        assertEquals("yes", dto.ok());

        // then URL built correctly
        String url = requestCaptor.getValue().url().toString();
        // starts with base/relative
        assertTrue(url.startsWith(baseUrl + "/weather?"));
        // contains all encoded params
        assertTrue(url.contains("apikey=api+key"));
        assertTrue(url.contains("city=BUE"));
        assertTrue(url.contains("q=a+b"));
    }

    @Test
    public void get_non2xx_throwsHttpInvocationException_afterParsingErrorBody() throws Exception {
        Map<String, String> defaults = Map.of("apikey", "k");
        Map<String, String> q = Map.of("city", "ERR");

        HttpClientService svc = new HttpClientService(baseUrl, http, mapper, defaults);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(http.newCall(requestCaptor.capture())).thenReturn(call);

        String errorJson = "{\"message\":\"bad code\"}";
        when(call.execute()).thenAnswer(inv -> new Response.Builder()
                .request(requestCaptor.getValue())
                .protocol(Protocol.HTTP_1_1)
                .code(400)
                .message("Bad Request")
                .body(ResponseBody.create(errorJson, MediaType.parse("application/json")))
                .build());

        assertThrows(HttpInvocationException.class,
                () -> svc.get("weather", q, SuccessDto.class, ErrorDto.class));
    }

    @Test
    public void get_ioFailure_wrapsAsHttpConnectionException() throws Exception {
        Map<String, String> defaults = Map.of();
        Map<String, String> q = Map.of();

        HttpClientService svc = new HttpClientService(baseUrl, http, mapper, defaults);

        when(http.newCall(any())).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("boom"));

        assertThrows(HttpConnectionException.class,
                () -> svc.get("ping", q, SuccessDto.class, ErrorDto.class));
    }

    @Test
    public void get_successButNullBody_throwsMissingResponseException() throws Exception {
        Map<String, String> defaults = Map.of();
        Map<String, String> q = Map.of();

        HttpClientService svc = new HttpClientService(baseUrl, http, mapper, defaults);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(http.newCall(requestCaptor.capture())).thenReturn(call);

        // No body at all
        when(call.execute()).thenAnswer(inv -> new Response.Builder()
                .request(requestCaptor.getValue())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(null)
                .build());

        assertThrows(MissingResponseException.class,
                () -> svc.get("ping", q, SuccessDto.class, ErrorDto.class));
    }

    @Test
    public void get_malformedJson_throwsResponseParsingException() throws Exception {
        Map<String, String> defaults = Map.of();
        Map<String, String> q = Map.of();

        HttpClientService svc = new HttpClientService(baseUrl, http, mapper, defaults);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(http.newCall(requestCaptor.capture())).thenReturn(call);

        String notJson = "<<<nope>>>";
        when(call.execute()).thenAnswer(inv -> new Response.Builder()
                .request(requestCaptor.getValue())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(notJson, MediaType.parse("application/json")))
                .build());

        assertThrows(ResponseParsingException.class,
                () -> svc.get("ping", q, SuccessDto.class, ErrorDto.class));
    }
}
