package com.optiprice.utils;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.FileWriter;

/**
 * Debugger to capture and save the actual Checkers API response
 */
public class CheckersApiDebugger {

    public static void main(String[] args) {
        String searchTerm = args.length > 0 ? args[0] : "milk";
        ObjectMapper objectMapper = new ObjectMapper();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false)
            );

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            System.out.println("=== CHECKERS API DEBUGGER ===");
            System.out.println("Search term: " + searchTerm);
            System.out.println();

            page.onResponse(response -> {
                String url = response.url();

                if (url.contains("/api/catalogue/get-products-filter")) {
                    try {
                        String responseBody = response.text();

                        System.out.println("✓ FOUND THE API!");
                        System.out.println("URL: " + url);
                        System.out.println("Status: " + response.status());
                        System.out.println("Response size: " + responseBody.length() + " characters");
                        System.out.println();

                        JsonNode jsonNode = objectMapper.readTree(responseBody);
                        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(jsonNode);

                        try (FileWriter writer = new FileWriter("checkers-products-response.json")) {
                            writer.write(prettyJson);
                        }

                        System.out.println("💾 Saved full response to: checkers-products-response.json");
                        System.out.println();

                        // Analyze structure
                        System.out.println("=== RESPONSE STRUCTURE ===");
                        jsonNode.fieldNames().forEachRemaining(fieldName -> {
                            JsonNode field = jsonNode.get(fieldName);
                            String type = field.isArray() ? "Array[" + field.size() + "]" :
                                    field.isObject() ? "Object" :
                                            field.isTextual() ? "String" :
                                                    field.isNumber() ? "Number" :
                                                            field.isBoolean() ? "Boolean" : "Unknown";
                            System.out.println("  " + fieldName + ": " + type);
                        });
                        System.out.println();

                        // Try to find products
                        System.out.println("=== LOOKING FOR PRODUCTS ===");
                        int productCount = 0;

                        for (String fieldName : new String[]{"products", "data", "items", "results"}) {
                            if (jsonNode.has(fieldName)) {
                                JsonNode node = jsonNode.get(fieldName);
                                if (node.isArray()) {
                                    productCount = node.size();
                                    System.out.println("Found array '" + fieldName + "' with " + productCount + " items");

                                    if (productCount > 0) {
                                        System.out.println("\nFirst product sample:");
                                        System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                                                .writeValueAsString(node.get(0)));
                                    }
                                    break;
                                }
                            }
                        }

                        if (productCount == 0) {
                            System.out.println("No standard product array found. Response might have nested structure.");
                            System.out.println("Check checkers-products-response.json for full details");
                        }

                    } catch (Exception e) {
                        System.err.println("Error processing response: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

            String searchUrl = "https://www.checkers.co.za/search?Search=" + searchTerm;
            System.out.println("Navigating to: " + searchUrl);
            System.out.println();

            page.navigate(searchUrl);

            System.out.println("Waiting 15 seconds for page to load...");
            page.waitForTimeout(15000);

            System.out.println("\nBrowser will stay open for 10 more seconds...");
            page.waitForTimeout(10000);

            browser.close();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}