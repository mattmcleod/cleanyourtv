package com.gathin.tvhome;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MoreBelowTest {
    @Test
    public void dockFocusHidesTheRowsAndShowsTheArrow() {
        assertTrue(MoreBelow.collapsed(0, 4));
        assertTrue(MoreBelow.collapsed(3, 4));
        assertTrue(MoreBelow.showArrow(3, 4, 20));
    }

    @Test
    public void gridFocusShowsTheRowsWithoutTheArrow() {
        assertFalse(MoreBelow.collapsed(4, 4));
        assertFalse(MoreBelow.showArrow(4, 4, 20));
    }

    @Test
    public void noArrowWhenNothingIsUnderTheDock() {
        assertFalse(MoreBelow.showArrow(0, 4, 4));
        assertFalse(MoreBelow.showArrow(0, 4, 3));
    }
}
