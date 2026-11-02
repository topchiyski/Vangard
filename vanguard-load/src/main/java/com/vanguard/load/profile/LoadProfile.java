package com.vanguard.load.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * How much load to generate, filled from the Gherkin table:
 *
 * <pre>
 * Given a load profile "product-browse" against the "products" endpoint:
 *   | users | rampUpSeconds | durationSeconds | thinkTimeMs |
 *   | 50    | 30            | 120             | 200         |
 * </pre>
 *
 * @param name            short name shown in reports
 * @param endpoint        endpoint name (see {@code LoadEndpoints}) or a path starting with "/"
 * @param users           concurrent virtual users at full load
 * @param rampUpSeconds   seconds to grow from 0 to {@code users}
 * @param durationSeconds seconds to hold full load after the ramp-up
 * @param thinkTimeMs     pause of each user between requests
 */
public record LoadProfile(String name, String endpoint, int users, int rampUpSeconds,
                          int durationSeconds, int thinkTimeMs) {

    /** System property with the path of a JSON file holding the profile, read by the Gatling JVM. */
    // WHY: a file, not inline JSON - Windows strips the quotes from JSON passed on a command line.
    public static final String SYSTEM_PROPERTY = "vanguard.load.profile.file";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Smoke caps keep PR builds fast and polite: -Dload.profile=full lifts them.
    static final int SMOKE_MAX_USERS = 5;
    static final int SMOKE_MAX_RAMP_SECONDS = 5;
    static final int SMOKE_MAX_DURATION_SECONDS = 15;

    public LoadProfile {
        if (users < 1 || rampUpSeconds < 0 || durationSeconds < 1 || thinkTimeMs < 0) {
            throw new IllegalArgumentException("Invalid load profile: users >= 1, rampUpSeconds >= 0, "
                    + "durationSeconds >= 1 and thinkTimeMs >= 0 are required");
        }
    }

    /** Builds a profile from one row of a Gherkin table; missing columns use sensible defaults. */
    public static LoadProfile fromTableRow(String name, String endpoint, Map<String, String> row) {
        return new LoadProfile(name, endpoint,
                Integer.parseInt(row.getOrDefault("users", "10")),
                Integer.parseInt(row.getOrDefault("rampUpSeconds", "10")),
                Integer.parseInt(row.getOrDefault("durationSeconds", "30")),
                Integer.parseInt(row.getOrDefault("thinkTimeMs", "200")));
    }

    /** Applies the smoke caps unless the full profile was requested. */
    public LoadProfile limitedTo(String loadProfileMode) {
        if ("full".equalsIgnoreCase(loadProfileMode)) {
            return this;
        }
        return new LoadProfile(name, endpoint,
                Math.min(users, SMOKE_MAX_USERS),
                Math.min(rampUpSeconds, SMOKE_MAX_RAMP_SECONDS),
                Math.min(durationSeconds, SMOKE_MAX_DURATION_SECONDS),
                thinkTimeMs);
    }

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Reads the profile inside the Gatling JVM. */
    public static LoadProfile fromSystemProperty() {
        String file = System.getProperty(SYSTEM_PROPERTY);
        if (file == null) {
            throw new IllegalStateException("No load profile given. Run simulations through a Gherkin "
                    + "load test (When I run the \"...\" load test), not directly.");
        }
        try {
            return MAPPER.readValue(new java.io.File(file), LoadProfile.class);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot read load profile from " + file, e);
        }
    }
}
