package com.gathin.tvhome;

import java.util.ArrayList;
import java.util.List;

/**
 * config.xml top_weather: the BoM Weather add-on's reading in the top bar's place. The
 * launcher only reads the add-on's provider; it makes no network request of its own.
 */
final class TopWeather {
    static final String AUTHORITY = "com.gathin.tvhome.weather";
    /** Read again this often while Home is on screen; the add-on itself asks BoM at most every 30 minutes. */
    static final long RELOAD_MS = 10 * 60 * 1000L;

    private TopWeather() {
    }

    /** BoM's forecast_icon_code as one emoji; "" for a code BoM has not published. */
    static String glyph(String code) {
        switch (code) {
            case "1": return "☀️";                        // sunny
            case "2": return "🌙";                        // clear (night)
            case "3": return "⛅";                              // partly cloudy
            case "4": return "☁️";                        // cloudy
            case "6": case "10": case "13": return "🌫️";   // hazy, fog, dusty
            case "8": case "11": case "17": return "🌦️";   // light rain, shower, light shower
            case "12": case "18": return "🌧️";      // rain, heavy shower
            case "9": return "💨";                        // windy
            case "14": case "15": return "❄️";            // frost, snow
            case "16": return "⛈️";                       // storm
            case "19": return "🌀";                       // cyclone
            default: return "";
        }
    }

    /** "Max 25° · Min 18° · 80% rain": the parts BoM sent, already formatted; empty ones are left out. */
    static String details(String... parts) {
        List<String> shown = new ArrayList<>();
        for (String p : parts) if (!p.isEmpty()) shown.add(p);
        return String.join(" \u00B7 ", shown);
    }
}
