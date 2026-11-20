package com.gathin.tvhome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class LiveLinksTest {
    @Test
    public void eachEntryBecomesACardInOrder() {
        List<ContinueWatching.Item> items = LiveLinks.parse(new String[]{
                "ABC TV|au.net.abc.iview|iview://video/LS1807V001S00?fromChannel=WatchLive",
                " SBS | com.sbs.ondemand.tv | https://www.sbs.com.au/ondemand/live "});

        assertEquals(2, items.size());
        assertEquals("ABC TV", items.get(0).title);
        assertEquals("au.net.abc.iview", items.get(0).pkg);
        assertEquals("iview://video/LS1807V001S00?fromChannel=WatchLive", items.get(0).intentUri);
        assertEquals("https://www.sbs.com.au/ondemand/live", items.get(1).intentUri);
    }

    @Test
    public void brokenEntriesAreSkipped() {
        assertTrue(LiveLinks.parse(new String[]{"", "No package", "A||uri", "A|pkg|", "A|pkg|uri|extra"}).isEmpty());
    }

    @Test
    public void emptyMeansNoRow() {
        assertTrue(LiveLinks.parse(new String[0]).isEmpty());
    }

    @Test
    public void chipTextIsWhichEverOfBlackOrWhiteReadsBetter() {
        assertEquals(0xFF000000, LiveLinks.textOn(0xFFFFD100));   // SBS yellow
        assertEquals(0xFF000000, LiveLinks.textOn(0xFF2DBDB6));   // iview teal
        assertEquals(0xFFFFFFFF, LiveLinks.textOn(0xFF1A2B5C));   // navy
        assertEquals(0xFFFFFFFF, LiveLinks.textOn(0xFF000000));
    }
}
