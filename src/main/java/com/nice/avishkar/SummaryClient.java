package com.nice.avishkar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class SummaryClient {

    private static final String HF_API_URL = "https://api-inference.huggingface.co/models/gpt2";
    private static final int TIMEOUT_MS = 8_000;

    private SummaryClient() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static String generateTravelSummary(OptimalTravelSchedule schedule, TravelRequest request) {
        String local = generateIntelligentSummary(schedule, request);
        if (local != null && !local.isEmpty()) {
            return local;
        }
        return "No routes available";
    }

    private static String generateIntelligentSummary(OptimalTravelSchedule schedule, TravelRequest request) {
        if (schedule == null || schedule.getRoutes() == null || schedule.getRoutes().isEmpty()) {
            return "No routes available";
        }

        String criteria = schedule.getCriteria().toLowerCase();
        long value = schedule.getValue();
        int segments = schedule.getRoutes().size();

        StringBuilder sb = new StringBuilder(140);
        sb.append("Optimal route from ").append(request.getSource())
                .append(" to ").append(request.getDestination()).append(": ");

        switch (criteria) {
            case "time": {
                long hours = value / 60;
                long minutes = value % 60;
                if (hours > 0) {
                    sb.append(hours).append("h");
                    if (minutes > 0) {
                        sb.append(" ").append(minutes).append("m");
                    }
                } else {
                    sb.append(minutes).append("m");
                }
                sb.append(" total");
                break;
            }
            case "cost": {
                sb.append("Costs Rs ").append(value);
                break;
            }
            default: {
                sb.append("Requires ").append(value).append(" hop");
                if (value != 1) {
                    sb.append("s");
                }
                break;
            }
        }

        if (segments > 1) {
            sb.append(", ").append(segments - 1).append(" connection");
            if (segments - 1 != 1) {
                sb.append("s");
            }
        }

        sb.append(".");
        return sb.toString();
    }

    @SuppressWarnings("unused")
    private static String callHuggingFaceAPI(String prompt) {
        try {
            URL url = new URL(HF_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(TIMEOUT_MS);
                conn.setReadTimeout(TIMEOUT_MS);
                conn.setDoOutput(true);

                String payload = String.format("{\"inputs\":\"%s\",\"parameters\":{\"max_length\":60}}", prompt.replace("\"", "\\\""));
                byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(bytes);
                }

                int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        // naive extraction
                        String json = sb.toString();
                        int idx = json.indexOf("\"generated_text\":\"");
                        if (idx >= 0) {
                            int start = idx + 17;
                            int end = json.indexOf("\"", start);
                            if (end > start) {
                                return json.substring(start, end);
                            }
                        }
                    }
                }
            } finally {
                conn.disconnect();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
