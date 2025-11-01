package com.nice.avishkar;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (verbose) {
            System.out.println("\n========== Processing Customer Requests ==========");
        }

        for (TravelRequest req : requests) {
            if (verbose) {
                System.out.println(String.format("Processing %s (%s->%s, criteria=%s)",
                        req.getRequestId(), req.getSource(), req.getDestination(), req.getCriteria()));
            }

            OptimalTravelSchedule sched = finder.findOptimalRoute(req);

            String summary;
            if (generateSummary) {
                summary = SummaryClient.generateTravelSummary(sched, req);
            } else {
                summary = sched.getRoutes().isEmpty() ? "No routes available" : "Not generated";
            }

            // Create a new immutable schedule with the correct summary
            OptimalTravelSchedule finalSchedule = new OptimalTravelSchedule(
                    sched.getRoutes(),
                    sched.getCriteria(),
                    sched.getValue(),
                    summary
            );

            results.put(req.getRequestId(), finalSchedule);
        }

        if (verbose) {
            System.out.println("========== Processing Complete ==========\n");
        }
        long end = System.currentTimeMillis();
        ResultInspector.printPerformanceMetrics(start, end, requests.size(), schedules.size());
        return results;
    }
}
