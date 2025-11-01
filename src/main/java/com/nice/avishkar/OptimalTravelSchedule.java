package com.nice.avishkar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class OptimalTravelSchedule {

    private final List<Route> routes;
    private final String criteria;
    private final long value;
    private final String summary;

    public OptimalTravelSchedule(List<Route> routes, String criteria, long value, String summary) {
        this.routes = Collections.unmodifiableList(
                new ArrayList<>(Objects.requireNonNull(routes, "Routes cannot be null"))
        );
        this.criteria = Objects.requireNonNull(criteria, "Criteria cannot be null");
        this.value = value;
        this.summary = Objects.requireNonNull(summary, "Summary cannot be null");
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public String getCriteria() {
        return criteria;
    }

    public long getValue() {
        return value;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OptimalTravelSchedule that = (OptimalTravelSchedule) o;
        return value == that.value
                && Objects.equals(routes, that.routes)
                && Objects.equals(criteria, that.criteria)
                && Objects.equals(summary, that.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routes, criteria, value, summary);
    }

    @Override
    public String toString() {
        return String.format("OptimalTravelSchedule[criteria=%s, value=%d, routes=%d]",
                criteria, value, routes.size());
    }
}
