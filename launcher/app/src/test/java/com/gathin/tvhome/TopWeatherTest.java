package com.gathin.tvhome;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TopWeatherTest {
    @Test
    public void detailsLeaveOutWhatBomDidNotSend() {
        assertEquals("Max 25° · Min 18° · 80% rain", TopWeather.details("Max 25°", "Min 18°", "80% rain"));
        assertEquals("Min 18° · 80% rain", TopWeather.details("", "Min 18°", "80% rain"));
        assertEquals("", TopWeather.details("", "", ""));
    }

    @Test
    public void everyPublishedIconHasAGlyph() {
        for (String code : new String[]{"1", "2", "3", "4", "6", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19"}) {
            assertEquals(code, false, TopWeather.glyph(code).isEmpty());
        }
        assertEquals("", TopWeather.glyph("99"));
    }
}
