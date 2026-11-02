package com.vanguard.load.profile;

import java.util.Map;

/**
 * Friendly endpoint names usable in Gherkin ({@code against the "products" endpoint}).
 * Add a line here to give a new endpoint a name. A value starting with "/" is used as a path directly.
 */
public final class LoadEndpoints {

    private static final Map<String, String> PATHS = Map.of(
            "health", "/health",
            // #{productId} is filled by the simulation's feeder with a random id.
            "products", "/products/#{productId}",
            "users", "/users/1");

    private LoadEndpoints() {
    }

    public static String pathFor(String endpoint) {
        if (endpoint.startsWith("/")) {
            return endpoint;
        }
        String path = PATHS.get(endpoint);
        if (path == null) {
            throw new IllegalArgumentException("Unknown load endpoint '" + endpoint + "'. Known: " + PATHS.keySet()
                    + ". Add it to LoadEndpoints or use a path like \"/health\".");
        }
        return path;
    }
}
