package com.nice.avishkar;

import java.util.List;
import java.util.Map;

public final class ResultInspector {

    private ResultInspector() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static void printResults(Map<String, OptimalTravelSchedule> results) {
        System.out.println("\n=== Travel Optimization Results ===\n");
        for (Map.Entry<String, OptimalTravelSchedule> e : results.entrySet()) {
            String requestId = e.getKey();
            OptimalTravelSchedule s = e.getValue();
            System.out.println("Request ID: " + requestId);
            System.out.println("  Criteria: " + s.getCriteria());
            System.out.println("  Value: " + s.getValue());
            System.out.println("  Routes: " + s.getRoutes().size());

            List<Route> routes = s.getRoutes();
            if (routes.isEmpty()) {
                System.out.println("  → No routes available");
            } else {
                for (int i = 0; i < routes.size(); i++) {
                    Route r = routes.get(i);
                    System.out.println(String.format("  → Route %d: %s → %s | %s | Dep: %s | Arr: %s",
                            i + 1, r.getSource(), r.getDestination(), r.getMode(), r.getDepartureTime(), r.getArrivalTime()));
                }
            }

            if (s.getSummary() != null && !s.getSummary().isEmpty()) {
                System.out.println("  Summary: " + s.getSummary());
            }
            System.out.println();
        }
    }

    public static boolean validate(Map<String, OptimalTravelSchedule> results, String requestId, String expectedCriteria, long expectedValue) {
        OptimalTravelSchedule s = results.get(requestId);
        if (s == null) {
            System.err.println("ERROR: Request " + requestId + " not found");
            return false;
        }
        boolean criteriaMatch = s.getCriteria().equalsIgnoreCase(expectedCriteria);
        boolean valueMatch = s.getValue() == expectedValue;
        if (!criteriaMatch) {
            System.err.println("ERROR: Criteria mismatch. Expected: " + expectedCriteria + ", Got: " + s.getCriteria());
        }
        if (!valueMatch) {
            System.err.println("ERROR: Value mismatch. Expected: " + expectedValue + ", Got: " + s.getValue());
        }
        return criteriaMatch && valueMatch;
    }

    public static void printPerformanceMetrics(long startTimeMs, long endTimeMs, int requestCount, int scheduleCount) {
        long duration = Math.max(0, endTimeMs - startTimeMs);
        double avg = requestCount > 0 ? (double) duration / requestCount : 0.0;
        System.out.println("\n=== Performance Metrics ===");
        System.out.println("Total Time: " + duration + " ms");
        System.out.println("Requests Processed: " + requestCount);
        System.out.println("Schedules Loaded: " + scheduleCount);
        System.out.println(String.format("Avg Time per Request: %.2f ms", avg));
        System.out.println("==========================\n");
    }
}
