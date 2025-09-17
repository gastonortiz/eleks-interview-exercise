package org.eleks.interview.exercise.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.eleks.interview.exercise.error.api.CodedException;
import org.eleks.interview.exercise.error.internal.HttpInvocationException;
import org.eleks.interview.exercise.model.ApplicationError;
import org.eleks.interview.exercise.model.CityCode;
import org.eleks.interview.exercise.model.Report;
import org.eleks.interview.exercise.model.Weather;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Map;

import static org.eleks.interview.exercise.model.ErrorCode.UNKNOWN_ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for WeatherApiServiceImpl behavior on success and error paths.
 */
public class WeatherApiServiceImplTest {

    private static final String BASE_URL = "http://api.weatherapi.com/v1";
    private static final String API_KEY  = "test-key";

    // Helpers to build the SUT as a spy so we can stub the protected super.get(...)
    private WeatherApiServiceImpl newSpyService() {
        OkHttpClient ok = new OkHttpClient();
        ObjectMapper om = new ObjectMapper();
        return spy(new WeatherApiServiceImpl(BASE_URL, API_KEY, ok, om));
    }

    @Test
    public void getWeather_delegatesToSuperGet_withCityNameQuery_andWrapsReportIntoWeather() {
        // given
        WeatherApiServiceImpl svc = newSpyService();
        CityCode bue = new CityCode("BUE", "Buenos Aires");
        Report report = mock(Report.class);

        // Capture the map passed to super.get(...) so we can assert q=<city name>
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

        // Stub the protected call in the superclass
        doReturn(report)
                .when(svc)
                .get(eq("current.json"), paramsCaptor.capture(), eq(Report.class), eq(ApplicationError.class));

        // when
        Weather result = svc.getWeather(bue);

        // then: verify delegation and correct query param
        verify(svc).get(eq("current.json"), anyMap(), eq(Report.class), eq(ApplicationError.class));
        Map<String, String> sentParams = paramsCaptor.getValue();
        assertEquals("Buenos Aires", sentParams.get("q"), "Should pass city name as 'q'");

        // then: Weather is created using the CityCode and Report we provided
        assertNotNull(result, "Weather must not be null");
        assertSame(bue, extractCityCode(result), "Weather should carry the same CityCode");
        assertSame(report, extractReport(result), "Weather should carry the same Report");

        // (Optional) timestamp sanity: ensure it's near 'now' (if your model exposes it)
        LocalDateTime ts = extractTimestampIfAvailable(result);
        if (ts != null) {
            LocalDateTime now = LocalDateTime.now();
            // allow a small delta (~5 seconds)
            assertTrue(!ts.isBefore(now.minusSeconds(5)) && !ts.isAfter(now.plusSeconds(5)),
                    "timestamp should be close to now");
        }
    }

    @Test
    public void getWeather_mapsHttpInvocationException_toCodedException_withParsedError() {
        // given
        WeatherApiServiceImpl svc = newSpyService();
        CityCode bad = new CityCode("XXX", "Unknown");

        ApplicationError parsed = new ApplicationError(UNKNOWN_ERROR, "invalid city");
        HttpInvocationException httpEx = new HttpInvocationException(parsed, BASE_URL + "/current.json", 400);

        doThrow(httpEx)
                .when(svc)
                .get(eq("current.json"), anyMap(), eq(Report.class), eq(ApplicationError.class));

        // when / then
        CodedException thrown = assertThrows(CodedException.class, () -> svc.getWeather(bad));
        assertSame(parsed, thrown.getError(), "CodedException should carry parsed ApplicationError");
        assertSame(httpEx, thrown.getCause(), "Original HttpInvocationException should be the cause");
    }

    /* ---------- Reflection helpers to keep the test resilient to DTO shapes ---------- */

    private static CityCode extractCityCode(Weather w) {
        try {
            // record-style accessor
            var m = w.getClass().getMethod("cityCode");
            return (CityCode) m.invoke(w);
        } catch (Exception ignored) {
            try {
                // bean-style getter
                var m = w.getClass().getMethod("getCityCode");
                return (CityCode) m.invoke(w);
            } catch (Exception ignoredToo) {
                // field access fallback
                try {
                    var f = w.getClass().getDeclaredField("cityCode");
                    f.setAccessible(true);
                    return (CityCode) f.get(w);
                } catch (Exception e) {
                    fail("Cannot extract CityCode from Weather; expose accessor 'cityCode()' or 'getCityCode()'");
                    return null; // unreachable, but keeps compiler happy
                }
            }
        }
    }

    private static Report extractReport(Weather w) {
        try {
            var m = w.getClass().getMethod("report");
            return (Report) m.invoke(w);
        } catch (Exception ignored) {
            try {
                var m = w.getClass().getMethod("getReport");
                return (Report) m.invoke(w);
            } catch (Exception ignoredToo) {
                try {
                    var f = w.getClass().getDeclaredField("report");
                    f.setAccessible(true);
                    return (Report) f.get(w);
                } catch (Exception e) {
                    fail("Cannot extract Report from Weather; expose accessor 'report()' or 'getReport()'");
                    return null;
                }
            }
        }
    }

    private static LocalDateTime extractTimestampIfAvailable(Weather w) {
        // Optional: only assert if your Weather exposes a timestamp; otherwise return null
        for (String name : new String[]{"timestamp", "time", "dateTime", "at"}) {
            try {
                var m = w.getClass().getMethod(name);
                var val = m.invoke(w);
                if (val instanceof LocalDateTime ldt) return ldt;
            } catch (Exception ignored) { }
            try {
                var m = w.getClass().getMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
                var val = m.invoke(w);
                if (val instanceof LocalDateTime ldt) return ldt;
            } catch (Exception ignored) { }
            try {
                var f = w.getClass().getDeclaredField(name);
                f.setAccessible(true);
                var val = f.get(w);
                if (val instanceof LocalDateTime ldt) return ldt;
            } catch (Exception ignored) { }
        }
        return null; // not exposed -> skip timestamp assertion
    }
}
