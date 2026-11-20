package com.gathin.tvhome.weather;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Reads the two Bureau of Meteorology feeds: a station's observations (JSON) and a state's town forecasts (XML). */
final class Bom {
    private Bom() {
    }

    /** Today's forecast for one town; a part BoM leaves out (the max, late in the day) stays empty. */
    static final class Today {
        String place = "", icon = "", max = "", min = "", rain = "";
    }

    // The observations list newest first, and air_temp appears only in the readings, so the first match is the
    // latest one. ponytail: a regex instead of a JSON parser, as org.json is not on the unit tests' classpath.
    private static final Pattern AIR_TEMP = Pattern.compile("\"air_temp\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");

    /** The latest air temperature, rounded to a whole degree; null when the station sent none. */
    static String airTemp(String json) {
        Matcher m = AIR_TEMP.matcher(json);
        return m.find() ? String.valueOf(Math.round(Double.parseDouble(m.group(1)))) : null;
    }

    /** The first forecast period of the area with this code; null when the file has no such area. */
    static Today today(InputStream xml, String aac) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setExpandEntityReferences(false);
        try {
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            // Android's parser has no such switch, and it never loads external entities anyway.
        }
        Document doc = f.newDocumentBuilder().parse(xml);
        NodeList areas = doc.getElementsByTagName("area");
        for (int i = 0; i < areas.getLength(); i++) {
            Element area = (Element) areas.item(i);
            if (!aac.equals(area.getAttribute("aac"))) continue;
            Today t = new Today();
            t.place = area.getAttribute("description");
            NodeList periods = area.getElementsByTagName("forecast-period");
            if (periods.getLength() == 0) return t;
            for (Node n = periods.item(0).getFirstChild(); n != null; n = n.getNextSibling()) {
                if (!(n instanceof Element)) continue;
                String type = ((Element) n).getAttribute("type"), value = n.getTextContent().trim();
                switch (type) {
                    case "forecast_icon_code": t.icon = value; break;
                    case "air_temperature_maximum": t.max = value; break;
                    case "air_temperature_minimum": t.min = value; break;
                    case "probability_of_precipitation": t.rain = value; break;
                    default: break;
                }
            }
            return t;
        }
        return null;
    }
}
