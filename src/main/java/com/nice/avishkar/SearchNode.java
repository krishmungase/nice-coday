package com.nice.avishkar;

import java.util.List;

/**
 * SearchNode - used in the priority queue during graph search.
 * Immutable fields for safety.
 */
public final class SearchNode implements Comparable<SearchNode> {
    private final String location;
    private final int totalTime;
    private final int totalCost;
    private final int totalHops;
    private final String arrivalTime;
    private final List<TravelSchedule> path; // read-only usage expected
    private final String criteria;

    public SearchNode(String location, int totalTime, int totalCost, int totalHops,
                      String arrivalTime, List<TravelSchedule> path, String criteria) {
        this.location = location;
        this.totalTime = totalTime;
        this.totalCost = totalCost;
        this.totalHops = totalHops;
        this.arrivalTime = arrivalTime;
        this.path = path;
        this.criteria = criteria;
    }

    public String getLocation() { return location; }
    public int getTotalTime() { return totalTime; }
    public int getTotalCost() { return totalCost; }
    public int getTotalHops() { return totalHops; }
    public String getArrivalTime() { return arrivalTime; }
    public List<TravelSchedule> getPath() { return path; }

    @Override
    public int compareTo(SearchNode other) {
        // Primary comparator based on criteria; tie-breakers applied in order
        if ("time".equalsIgnoreCase(criteria)) {
            if (this.totalTime != other.totalTime) return Integer.compare(this.totalTime, other.totalTime);
            if (this.totalCost != other.totalCost) return Integer.compare(this.totalCost, other.totalCost);
            return Integer.compare(this.totalHops, other.totalHops);
        } else if ("cost".equalsIgnoreCase(criteria)) {
            if (this.totalCost != other.totalCost) return Integer.compare(this.totalCost, other.totalCost);
            if (this.totalTime != other.totalTime) return Integer.compare(this.totalTime, other.totalTime);
            return Integer.compare(this.totalHops, other.totalHops);
        } else {
            if (this.totalHops != other.totalHops) return Integer.compare(this.totalHops, other.totalHops);
            if (this.totalTime != other.totalTime) return Integer.compare(this.totalTime, other.totalTime);
            return Integer.compare(this.totalCost, other.totalCost);
        }
    }

    public String stateKey() {
        // arrivalTime included to distinguish different visit times
        return location + "_" + (arrivalTime == null ? "NA" : arrivalTime);
    }
}
