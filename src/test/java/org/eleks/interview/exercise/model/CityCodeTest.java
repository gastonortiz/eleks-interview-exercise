package org.eleks.interview.exercise.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CityCodeTest {

    @Test
    public void canonicalConstructor_exposesComponents_andToString() {
        CityCode cc = new CityCode("BUE", "Buenos Aires");

        assertEquals("BUE", cc.code());
        assertEquals("Buenos Aires", cc.name());
        assertEquals("BUE (Buenos Aires)", cc.toString());
    }

    @Test
    public void mapConstructor_reads_city_code_and_name() {
        CityCode cc = new CityCode(Map.of(
                "city_code", "NYC",
                "name", "New York"
        ));

        assertEquals("NYC", cc.code());
        assertEquals("New York", cc.name());
        assertEquals("NYC (New York)", cc.toString());
    }

    @Test
    public void equalsAndHashCode_areBasedOnComponents() {
        CityCode a = new CityCode("TYO", "Tokyo");
        CityCode b = new CityCode("TYO", "Tokyo");
        CityCode c = new CityCode("OSA", "Osaka");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    public void toString_handlesNulls_gracefully() {
        // record components allow nulls; verify toString doesn't throw
        assertEquals("null (City)", new CityCode(null, "City").toString());
        assertEquals("BUE (null)", new CityCode("BUE", null).toString());
    }
}
