package org.eleks.interview.exercise.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.eleks.interview.exercise.error.api.CodedException;
import org.eleks.interview.exercise.error.internal.HttpInvocationException;
import org.eleks.interview.exercise.model.ApplicationError;
import org.eleks.interview.exercise.model.CityCode;
import org.eleks.interview.exercise.model.Report;
import org.eleks.interview.exercise.model.Weather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import static org.eleks.interview.exercise.model.ErrorCode.UNKNOWN_ERROR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for WeatherApiServiceImpl behavior on success and error paths.
 */
@ExtendWith(MockitoExtension.class)
public class WeatherApiServiceImplTest {
    private static final String MOCK_RESPONSE_BODY = "whatever";
    private static final CityCode MOCK_CITY_CODE = new CityCode("TYO", "Tokyo");

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private OkHttpClient okHttpClient;
    @Mock
    private Response response;

    private WeatherService service;

    @BeforeEach
    public void setUp() throws IOException {
        Call call = mock(Call.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        when(responseBody.string()).thenReturn(MOCK_RESPONSE_BODY);
        when(response.body()).thenReturn(responseBody);
        when(call.execute()).thenReturn(response);
        when(okHttpClient.newCall(any())).thenReturn(call);
        service = new WeatherApiServiceImpl("http://api.weatherapi.com/v1", "test-key", okHttpClient, objectMapper);
    }

    @Test
    public void getWeatherTest() throws JsonProcessingException {
        when(response.isSuccessful()).thenReturn(true);
        Report mockReport = mock(Report.class);
        when(objectMapper.readValue(eq(MOCK_RESPONSE_BODY), eq(Report.class))).thenReturn(mockReport);

        Weather weather = service.getWeather(MOCK_CITY_CODE);
        assertThat(weather.report(), equalTo(mockReport));
        assertThat(weather.cityCode(), equalTo(MOCK_CITY_CODE));
        assertThat(weather.timestamp(), notNullValue());
    }

    @Test
    public void getWeatherFailedTest() throws JsonProcessingException {
        when(response.isSuccessful()).thenReturn(false);
        ApplicationError mockError = mock(ApplicationError.class);
        when(objectMapper.readValue(eq(MOCK_RESPONSE_BODY), eq(ApplicationError.class))).thenReturn(mockError);
        CodedException thrown = assertThrows(CodedException.class, () -> service.getWeather(MOCK_CITY_CODE));
        assertThat(thrown.getError(), equalTo(mockError));
    }
}
