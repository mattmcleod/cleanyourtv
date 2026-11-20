package com.gathin.tvhome;

import java.util.ArrayList;
import java.util.List;

/**
 * config.xml live_links: a row of fixed cards just above the dock, each opening one app's own live stream with a
 * deep link ("Title|package|uri"). Empty, the default, means no row.
 */
final class LiveLinks {
    private LiveLinks() {
    }

    /** The entries that have all three parts; anything else is skipped, so one typo costs one card. */
    static List<ContinueWatching.Item> parse(String[] entries) {
        List<ContinueWatching.Item> items = new ArrayList<>();
        for (String entry : entries) {
            String[] p = entry.split("\\|", -1);
            if (p.length != 3) continue;
            String title = p[0].trim(), pkg = p[1].trim(), uri = p[2].trim();
            if (title.isEmpty() || pkg.isEmpty() || uri.isEmpty()) continue;
            items.add(new ContinueWatching.Item(pkg, title, "", uri, -1f, null, 0));
        }
        return items;
    }

    /** Black or white, whichever contrasts more with this colour (WCAG relative luminance). */
    static int textOn(int argb) {
        double l = 0.2126 * channel(argb >> 16) + 0.7152 * channel(argb >> 8) + 0.0722 * channel(argb);
        // Contrast with black is (l + 0.05) / 0.05, with white 1.05 / (l + 0.05): equal at l = 0.179.
        return l > 0.179 ? 0xFF000000 : 0xFFFFFFFF;
    }

    private static double channel(int v) {
        double c = (v & 0xFF) / 255.0;
        return c <= 0.04045 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }
}
