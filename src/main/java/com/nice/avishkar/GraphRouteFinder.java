package com.nice.avishkar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public final class GraphRouteFinder {

    private final Map<String, List<TravelSchedule>> adjacency;

    public GraphRouteFinder(List<TravelSchedule> schedules) {
        this.adjacency = buildAdjacency(schedules);
    }

    private Map<String, List<TravelSchedule>> buildAdjacency(List<TravelSchedule> schedules) {
        int expected = Math.max(16, schedules.size() / 3);
        Map<String, List<TravelSchedule>> graph = new HashMap<>(expected);
        for (TravelSchedule s : schedules) {
            graph.computeIfAbsent(s.getSource(), k -> new ArrayList<>(4)).add(s);
        }
        return graph;
    }

    private static int costByCriteria(int time, int cost, int hops, String criteria) {
        if ("time".equalsIgnoreCase(criteria)) {
            return time;
        }
        if ("cost".equalsIgnoreCase(criteria)) {
            return cost;
        }
        return hops;
    }

    public OptimalTravelSchedule findOptimalRoute(TravelRequest request) {
        String source = request.getSource();
        String destination = request.getDestination();
        String criteria = request.getCriteria();

        if (source.equals(destination)) {
            return new OptimalTravelSchedule(Collections.emptyList(), criteria, 0L, "No routes available");
        }

        PriorityQueue<SearchNode> pq = new PriorityQueue<>();
        Map<String, Integer> bestCost = new HashMap<>(64); // stateKey -> best cost

        SearchNode start = new SearchNode(source, 0, 0, 0, null, new ArrayList<>(8), criteria);
        pq.offer(start);
        bestCost.put(start.stateKey(), 0);

        while (!pq.isEmpty()) {
            SearchNode cur = pq.poll();

            if (cur.getLocation().equals(destination)) {
                // Build route list from TravelSchedule -> Route DTOs
                List<Route> routes = new ArrayList<>(cur.getPath().size());
                for (TravelSchedule ts : cur.getPath()) {
                    routes.add(new Route(ts.getSource(), ts.getDestination(), ts.getMode(), ts.getDepartureTime(), ts.getArrivalTime()));
                }
                long value = ("time".equalsIgnoreCase(criteria)) ? cur.getTotalTime()
                        : ("cost".equalsIgnoreCase(criteria)) ? cur.getTotalCost()
                        : cur.getTotalHops();
                return new OptimalTravelSchedule(routes, criteria, value, "");
            }

            Integer recorded = bestCost.get(cur.stateKey());
            if (recorded != null && recorded < costByCriteria(cur.getTotalTime(), cur.getTotalCost(), cur.getTotalHops(), criteria)) {
                continue;
            }

            List<TravelSchedule> neigh = adjacency.get(cur.getLocation());
            if (neigh == null) {
                continue;
            }

            for (TravelSchedule edge : neigh) {
                String nextLoc = edge.getDestination();

                int addedWait = 0;
                if (cur.getArrivalTime() != null) {
                    addedWait = TimeHelper.waitingMinutes(cur.getArrivalTime(), edge.getDepartureTime());
                }
                int newTime = cur.getTotalTime() + addedWait + edge.getDurationMinutes();
                int newCost = cur.getTotalCost() + edge.getCost();
                int newHops = cur.getTotalHops() + 1;

                int nextKeyCost = costByCriteria(newTime, newCost, newHops, criteria);
                String nextStateKey = nextLoc + "_" + edge.getArrivalTime();

                Integer prevBest = bestCost.get(nextStateKey);
                if (prevBest != null && prevBest <= nextKeyCost) {
                    continue;
                }

                List<TravelSchedule> newPath = new ArrayList<>(cur.getPath().size() + 1);
                newPath.addAll(cur.getPath());
                newPath.add(edge);

                SearchNode nextNode = new SearchNode(nextLoc, newTime, newCost, newHops, edge.getArrivalTime(), newPath, criteria);
                pq.offer(nextNode);
                bestCost.put(nextStateKey, nextKeyCost);
            }
        }

        return new OptimalTravelSchedule(Collections.emptyList(), request.getCriteria(), 0L, "No routes available");
    }
}
