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
}
