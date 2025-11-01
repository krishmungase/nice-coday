package com.nice.avishkar;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main implementation of Travel Optimizer
 * Uses GraphRouteFinder and DataLoader
 */
public class TravelOptimizerImpl implements ITravelOptimizer {

    private final boolean generateSummary;
    private final boolean verbose;

    public TravelOptimizerImpl(boolean generateSummary) {
        this.generateSummary = generateSummary;
        this.verbose = "true".equalsIgnoreCase(System.getProperty("verbose", "true"));
    }

    @Override
    public Map<String, OptimalTravelSchedule> getOptimalTravelOptions(ResourceInfo resourceInfo) throws IOException {
        long start = System.currentTimeMillis();
        List<TravelSchedule> schedules = DataLoader.readSchedules(resourceInfo.getTransportSchedulePath());
        List<TravelRequest> requests = DataLoader.readCustomerRequests(resourceInfo.getCustomerRequestPath());

        GraphRouteFinder finder = new GraphRouteFinder(schedules);
        Map<String, OptimalTravelSchedule> results = new HashMap<>(Math.max(16, requests.size()));

        if (verbose) System.out.println("\n========== Processing Customer Requests ==========");

        for (TravelRequest req : requests) {
            if (verbose) System.out.println(String.format("Processing %s (%s->%s, criteria=%s)",
                    req.getRequestId(), req.getSource(), req.getDestination(), req.getCriteria()));

            OptimalTravelSchedule sched = finder.findOptimalRoute(req);

            if (generateSummary) {
                String sum = SummaryClient.generateTravelSummary(sched, req);
                sched.setSummary(sum);
            } else {
                if (sched.getRoutes().isEmpty()) sched.setSummary("No routes available");
                else sched.setSummary("Not generated");
            }

            results.put(req.getRequestId(), sched);
        }

        if (verbose) System.out.println("========== Processing Complete ==========\n");
        long end = System.currentTimeMillis();
        ResultInspector.printPerformanceMetrics(start, end, requests.size(), schedules.size());
        return results;
    }
}