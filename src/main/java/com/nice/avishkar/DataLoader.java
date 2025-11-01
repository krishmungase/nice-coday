package com.nice.avishkar;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public final class DataLoader {

    private DataLoader() {}

    public static List<TravelSchedule> readSchedules(Path filePath) throws IOException {
        List<TravelSchedule> schedules = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // Use split with limit to avoid accidental commas in later fields
                String[] parts = line.split(",", 6);
                if (parts.length < 6) continue;
                String src = parts[0].trim();
                String dst = parts[1].trim();
                String mode = parts[2].trim();
                String dep = parts[3].trim();
                String arr = parts[4].trim();
                String costStr = parts[5].trim();
                if (src.isEmpty() || dst.isEmpty() || dep.isEmpty() || arr.isEmpty()) continue;
                int cost;
                try {
                    cost = Integer.parseInt(costStr);
                } catch (NumberFormatException e) {
                    continue;
                }
                schedules.add(new TravelSchedule(src, dst, mode, dep, arr, cost));
            }
        }
        return schedules;
    }

    public static List<TravelRequest> readCustomerRequests(Path filePath) throws IOException {
        List<TravelRequest> requests = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 5);
                if (parts.length < 5) continue;
                String requestId = parts[0].trim();
                String customerName = parts[1].trim();
                String source = parts[2].trim();
                String destination = parts[3].trim();
                String criteria = parts[4].trim().toLowerCase(Locale.ROOT);
                if (requestId.isEmpty() || source.isEmpty() || destination.isEmpty()) continue;
                if (!(criteria.equals("time") || criteria.equals("cost") || criteria.equals("hops"))) {
                    // default to time if invalid
                    criteria = "time";
                }
                requests.add(new TravelRequest(requestId, customerName, source, destination, criteria));
            }
        }
        return requests;
    }
}
