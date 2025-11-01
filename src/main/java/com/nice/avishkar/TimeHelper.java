package com.nice.avishkar;

/**
 * TimeHelper - utility for HH:mm time computations
 * - Minimal object allocations
 * - Robust to invalid input through NumberFormatException
 */
public final class TimeHelper {

    private TimeHelper() {}

    public static int parseToMinutes(String hhmm) {
        if (hhmm == null) throw new IllegalArgumentException("time string is null");
        // Expecting "HH:mm" — fast parse without regex
        int colon = hhmm.indexOf(':');
        if (colon < 0) throw new IllegalArgumentException("invalid time format: " + hhmm);
        int h = Integer.parseInt(hhmm.substring(0, colon));
        int m = Integer.parseInt(hhmm.substring(colon + 1));
        return h * 60 + m;
    }

    /**
     * Calculate duration from departure to arrival in minutes, supports next-day arrival
     */
    public static int durationMinutes(String departure, String arrival) {
        int dep = parseToMinutes(departure);
        int arr = parseToMinutes(arrival);
        int diff = arr - dep;
        if (diff < 0) diff += 24 * 60;
        return diff;
    }

    /**
     * Waiting time from arrival to next departure (supports overnight)
     */
    public static int waitingMinutes(String arrival, String nextDeparture) {
        int arr = parseToMinutes(arrival);
        int dep = parseToMinutes(nextDeparture);
        int diff = dep - arr;
        if (diff < 0) diff += 24 * 60;
        return diff;
    }

    public static boolean isBeforeOrEqual(String t1, String t2) {
        return parseToMinutes(t1) <= parseToMinutes(t2);
    }
}
