package com.nice.avishkar;

/**
 * TravelSchedule - immutable model representing a single transport segment
 */
public final class TravelSchedule {
    private final String source;
    private final String destination;
    private final String mode;
    private final String departureTime;
    private final String arrivalTime;
    private final int cost;
    private final int durationMinutes;

    public TravelSchedule(String source, String destination, String mode,
                          String departureTime, String arrivalTime, int cost) {
        this.source = source;
        this.destination = destination;
        this.mode = mode;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.cost = cost;
        this.durationMinutes = TimeHelper.durationMinutes(departureTime, arrivalTime);
    }

    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getMode() { return mode; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public int getCost() { return cost; }
    public int getDurationMinutes() { return durationMinutes; }
}
