package org.eleks.interview.exercise.model.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.eleks.interview.exercise.model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class ReportDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Report.class, new ReportDeserializer());
        mapper.registerModule(module);
    }

    @Test
    public void deserializesValidPayload() throws Exception {
        String json = """
            {
              "current": {
                "temp_c": 19.4,
                "condition": { "text": "Sunny" },
                "humidity": 73
              }
            }
            """;

        Report r = mapper.readValue(json, Report.class);

        assertEquals(19.4, readTemp(r), 1e-6);
        assertEquals("Sunny", readDescription(r));
        assertEquals(73, readHumidity(r));
    }

    @Test
    public void tempAcceptsIntegerNumbersToo() throws Exception {
        String json = """
            {
              "current": {
                "temp_c": 20,
                "condition": { "text": "Cloudy" },
                "humidity": 50
              }
            }
            """;

        Report r = mapper.readValue(json, Report.class);

        assertEquals(20.0, readTemp(r), 1e-6);
        assertEquals("Cloudy", readDescription(r));
        assertEquals(50, readHumidity(r));
    }

    @Test
    public void missingCurrentNode_throws() {
        String json = "{}";
        assertThrows(NullPointerException.class, () -> mapper.readValue(json, Report.class));
    }

    @Test
    public void missingRequiredField_throws() {
        // Missing "humidity"
        String json = """
            {
              "current": {
                "temp_c": 15.5,
                "condition": { "text": "Rain" }
              }
            }
            """;
        assertThrows(NullPointerException.class, () -> mapper.readValue(json, Report.class));
    }

    /* ---------- Reflection helpers (support record/POJO/field) ---------- */

    private static double readTemp(Report r) {
        for (String n : new String[]{"temp", "tempC", "temperature", "getTemp", "getTempC"}) {
            Double v = tryCallDouble(r, n);
            if (v != null) return v;
        }
        Double f = tryReadDoubleField(r, "temp", "tempC", "temperature");
        if (f != null) return f;
        fail("Could not extract temperature from Report; expose temp()/getTemp()/temp field");
        return 0;
    }

    private static String readDescription(Report r) {
        for (String n : new String[]{"description", "text", "getDescription", "getText"}) {
            String v = tryCallString(r, n);
            if (v != null) return v;
        }
        String f = tryReadStringField(r, "description", "text");
        if (f != null) return f;
        fail("Could not extract description from Report; expose description()/getDescription()/field");
        return null;
    }

    private static int readHumidity(Report r) {
        for (String n : new String[]{"humidity", "getHumidity"}) {
            Integer v = tryCallInt(r, n);
            if (v != null) return v;
        }
        Integer f = tryReadIntField(r, "humidity");
        if (f != null) return f;
        fail("Could not extract humidity from Report; expose humidity()/getHumidity()/field");
        return 0;
    }

    /* --- small reflection utilities --- */
    private static Double tryCallDouble(Object o, String method) {
        try { Method m = findMethod(o, method); if (m != null) return ((Number) m.invoke(o)).doubleValue(); } catch (Exception ignored) {}
        return null;
    }
    private static Integer tryCallInt(Object o, String method) {
        try { Method m = findMethod(o, method); if (m != null) return ((Number) m.invoke(o)).intValue(); } catch (Exception ignored) {}
        return null;
    }
    private static String tryCallString(Object o, String method) {
        try { Method m = findMethod(o, method); if (m != null) return (String) m.invoke(o); } catch (Exception ignored) {}
        return null;
    }
    private static Double tryReadDoubleField(Object o, String... fields) {
        for (String f : fields) try { Field fld = findField(o, f); if (fld != null) return ((Number) fld.get(o)).doubleValue(); } catch (Exception ignored) {}
        return null;
    }
    private static Integer tryReadIntField(Object o, String... fields) {
        for (String f : fields) try { Field fld = findField(o, f); if (fld != null) return ((Number) fld.get(o)).intValue(); } catch (Exception ignored) {}
        return null;
    }
    private static String tryReadStringField(Object o, String... fields) {
        for (String f : fields) try { Field fld = findField(o, f); if (fld != null) return (String) fld.get(o); } catch (Exception ignored) {}
        return null;
    }
    private static Method findMethod(Object o, String name) {
        try { return o.getClass().getMethod(name); } catch (NoSuchMethodException e) { return null; }
    }
    private static Field findField(Object o, String name) {
        try { Field f = o.getClass().getDeclaredField(name); f.setAccessible(true); return f; } catch (NoSuchFieldException e) { return null; }
    }
}
