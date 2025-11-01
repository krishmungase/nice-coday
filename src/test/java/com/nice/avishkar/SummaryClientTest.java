package com.nice.avishkar;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SummaryClientTest {

    @Test
    public void testLocalSummaryGeneration() {
        // Test with a simple route
        List<Route> routes = new ArrayList<>();
        routes.add(new Route("A", "B", "Bus", "10:00", "10:30"));

        TravelRequest request = new TravelRequest("1", "John Doe", "A", "B", "time");
        OptimalTravelSchedule schedule = new OptimalTravelSchedule(routes, "time", 30L, "");

        String summary = SummaryClient.generateTravelSummary(schedule, request);
        System.out.println("Local Summary: " + summary);
    }

    @Test
    public void testHuggingFaceAPI() throws Exception {
        // Test the Hugging Face API call directly using reflection
        System.out.println("\n========== Testing Hugging Face API ==========");

        List<Route> routes = new ArrayList<>();
        routes.add(new Route("New York", "Boston", "Train", "08:00", "12:00"));

        TravelRequest request = new TravelRequest("test-1", "Jane Smith", "New York", "Boston", "time");
        OptimalTravelSchedule schedule = new OptimalTravelSchedule(routes, "time", 240L, "");

        // Use reflection to call the private methods
        Method buildPromptMethod = SummaryClient.class.getDeclaredMethod("buildPrompt", OptimalTravelSchedule.class, TravelRequest.class);
        buildPromptMethod.setAccessible(true);
        String prompt = (String) buildPromptMethod.invoke(null, schedule, request);
        System.out.println("Generated Prompt: " + prompt);

        // Check if token is available
        String token = System.getenv("HUGGINGFACE_HUB_API_TOKEN");
        if (token == null) {
            token = System.getenv("HF_API_TOKEN");
        }
        if (token != null && !token.isEmpty()) {
            System.out.println("API Token found: " + token.substring(0, Math.min(10, token.length())) + "...");
        } else {
            System.out.println("WARNING: No API token found in environment variables");
        }

        Method callAPIMethod = SummaryClient.class.getDeclaredMethod("callHuggingFaceAPI", String.class);
        callAPIMethod.setAccessible(true);
        String apiResult = (String) callAPIMethod.invoke(null, prompt);

        if (apiResult != null && !apiResult.isEmpty()) {
            System.out.println("SUCCESS! API Response: " + apiResult);
        } else {
            System.out.println("API Call returned null (check error messages above for details)");
        }
        System.out.println("=============================================\n");
    }

    @Test
    public void testSummaryGeneration() {
        List<Route> routes = new ArrayList<>();
        routes.add(new Route("New York", "Boston", "Train", "08:00", "12:00"));

        TravelRequest request = new TravelRequest("test-1", "Jane Smith", "New York", "Boston", "time");
        OptimalTravelSchedule schedule = new OptimalTravelSchedule(routes, "time", 240L, "");

        String summary = SummaryClient.generateTravelSummary(schedule, request);
        System.out.println("\n========== Summary Test ==========");
        System.out.println("Generated Summary: " + summary);
        System.out.println("====================================\n");
    }

    @Test
    public void testMultipleCriteria() {
        List<Route> routes = new ArrayList<>();
        routes.add(new Route("Point A", "Point B", "Bus", "09:00", "11:00"));
        routes.add(new Route("Point B", "Point C", "Train", "11:30", "14:00"));

        TravelRequest request = new TravelRequest("test-2", "Customer", "Point A", "Point C", "cost");
        OptimalTravelSchedule schedule = new OptimalTravelSchedule(routes, "cost", 350L, "");

        String summary = SummaryClient.generateTravelSummary(schedule, request);
        System.out.println("Cost-based Summary: " + summary);

        // Test hops
        schedule = new OptimalTravelSchedule(routes, "hops", 2L, "");
        summary = SummaryClient.generateTravelSummary(schedule, request);
        System.out.println("Hops-based Summary: " + summary);
    }
}
