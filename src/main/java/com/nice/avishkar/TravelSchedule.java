package com.nice.avishkar;

import java.util.Objects;

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
        this.source = Objects.requireNonNull(source, "Source cannot be null");
        this.destination = Objects.requireNonNull(destination, "Destination cannot be null");
        this.mode = Objects.requireNonNull(mode, "Mode cannot be null");
        this.departureTime = Objects.requireNonNull(departureTime, "Departure time cannot be null");
        this.arrivalTime = Objects.requireNonNull(arrivalTime, "Arrival time cannot be null");
        if (cost < 0) {
            throw new IllegalArgumentException("Cost must be non-negative: " + cost);
        }
        this.cost = cost;
        this.durationMinutes = TimeHelper.durationMinutes(departureTime, arrivalTime);
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getMode() {
        return mode;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public int getCost() {
        return cost;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TravelSchedule that = (TravelSchedule) o;
        return cost == that.cost
                && durationMinutes == that.durationMinutes
                && Objects.equals(source, that.source)
                && Objects.equals(destination, that.destination)
                && Objects.equals(mode, that.mode)
                && Objects.equals(departureTime, that.departureTime)
                && Objects.equals(arrivalTime, that.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination, mode, departureTime, arrivalTime, cost, durationMinutes);
    }

    @Override
    public String toString() {
        return String.format("TravelSchedule[%s -> %s via %s, Dep: %s, Arr: %s, Cost: %d]",
                source, destination, mode, departureTime, arrivalTime, cost);
    }
}
