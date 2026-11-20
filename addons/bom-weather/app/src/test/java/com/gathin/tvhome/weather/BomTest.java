package com.gathin.tvhome.weather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class BomTest {
    private static final String XML = "<?xml version=\"1.0\"?><product><forecast>"
            + "<area aac=\"VIC_PT001\" description=\"Elsewhere\" type=\"location\"><forecast-period index=\"0\">"
            + "<element type=\"air_temperature_maximum\">40</element></forecast-period></area>"
            + "<area aac=\"VIC_PT042\" description=\"Melbourne\" type=\"location\">"
            + "<forecast-period index=\"0\"><element type=\"forecast_icon_code\">16</element>"
            + "<text type=\"precis\">Showers. Possible storm.</text>"
            + "<text type=\"probability_of_precipitation\">80%</text></forecast-period>"
            + "<forecast-period index=\"1\"><element type=\"air_temperature_maximum\">25</element></forecast-period>"
            + "</area></forecast></product>";

    private static Bom.Today today(String aac) throws Exception {
        return Bom.today(new ByteArrayInputStream(XML.getBytes(StandardCharsets.UTF_8)), aac);
    }

    @Test
    public void latestReadingIsTheFirstAndRounded() {
        assertEquals("18", Bom.airTemp("{\"header\":[{}],\"data\":[{\"air_temp\": 18.4},{\"air_temp\": 12.0}]}"));
        assertEquals("-3", Bom.airTemp("{\"data\":[{\"air_temp\":-2.6}]}"));
        assertNull(Bom.airTemp("{\"data\":[{\"air_temp\":null}]}"));
    }

    @Test
    public void todayIsTheFirstPeriodOfThatArea() throws Exception {
        Bom.Today t = today("VIC_PT042");
        assertEquals("Melbourne", t.place);
        assertEquals("16", t.icon);
        assertEquals("80%", t.rain);
        assertEquals("", t.max);   // late in the day BoM drops today's max; tomorrow's is not used instead
    }

    @Test
    public void unknownAreaIsNull() throws Exception {
        assertNull(today("NSW_PT131"));
    }
}
