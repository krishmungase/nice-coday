package com.nice.avishkar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class SummaryClient {

    private static final String HF_API_URL = "https://api-inference.huggingface.co/models/openai-community/gpt2";
    private static final int TIMEOUT_MS = 45_000; // Increased timeout for model loading

    private static final String HF_API_TOKEN = System.getenv("HF_API_TOKEN") != null
            ? System.getenv("HF_API_TOKEN")
            : System.getenv("HUGGINGFACE_HUB_API_TOKEN");

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

    private static String buildPrompt(OptimalTravelSchedule schedule, TravelRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Write a 60-word travel summary. The customer wanted the '")
                .append(schedule.getCriteria()).append("' optimized route from ")
                .append(request.getSource()).append(" to ").append(request.getDestination())
                .append(". Their route requires ").append(schedule.getValue())
                .append(" (").append(schedule.getCriteria()).append(") with ")
                .append(schedule.getRoutes().size()).append(" segments.");
        return prompt.toString();
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

    private static String callHuggingFaceAPI(String prompt) {
        try {
            URL url = new URL(HF_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                // Add authentication header if token is available
                if (HF_API_TOKEN != null && !HF_API_TOKEN.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + HF_API_TOKEN);
                }
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
                } else {
                    StringBuilder errorMsg = new StringBuilder();
                    try {
                        if (conn.getErrorStream() != null) {
                            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                                String line;
                                while ((line = br.readLine()) != null) {
                                    errorMsg.append(line);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }

                    String errorResponse = errorMsg.toString();

                    if (code == 503) {
                        System.err.println("API Error (503): Model is loading. Please wait and try again.");
                    } else if (code == 401) {
                        System.err.println("API Error (401): Authentication failed. Your token may be expired or invalid.");
                        System.err.println("Get a new token at: https://huggingface.co/settings/tokens");
                    } else if (code == 403) {
                        if (errorResponse.contains("sufficient permissions") || errorResponse.contains("Inference Providers")) {
                            System.err.println("API Error (403): Token lacks 'Make calls to Inference Providers' permission.");
                            System.err.println("Solution: Create a NEW token with this permission:");
                            System.err.println("  1. Go to: https://huggingface.co/settings/tokens");
                            System.err.println("  2. Click 'New token'");
                            System.err.println("  3. Select 'Read' permission");
                            System.err.println("  4. IMPORTANT: Check 'Make calls to Inference Providers'");
                            System.err.println("  5. Create and use the new token");
                        } else {
                            System.err.println("API Error (403): Access forbidden. " + errorResponse);
                        }
                    } else if (code == 404) {
                        System.err.println("API Error (404): Model or endpoint not found.");
                        System.err.println("Possible causes:");
                        System.err.println("  1. Model not available via Inference API");
                        System.err.println("  2. Token lacks proper permissions");
                    } else {
                        System.err.println("API Error Response (code " + code + "): " + errorResponse);
                    }
                }
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
        }
        return null;
    }
}
