package com.gathin.tvhome;

/**
 * config.xml more_arrow: the dock sits DROP_DP lower, the grid rows under it are clipped away while focus is on
 * the dock (or above it), and a down chevron at the bottom of the screen says there is more. Down into the grid
 * brings the rows back as usual.
 */
final class MoreBelow {
    /** How much lower the dock sits than without the switch. */
    static final float DROP_DP = 32;

    private MoreBelow() {
    }

    /** Focus on the dock row: the rows under it are hidden. */
    static boolean collapsed(int focusedPosition, int columns) {
        return focusedPosition < columns;
    }

    /** The chevron: only while collapsed, and only when there is a row under the dock to go to. */
    static boolean showArrow(int focusedPosition, int columns, int appCount) {
        return collapsed(focusedPosition, columns) && appCount > columns;
    }
}
