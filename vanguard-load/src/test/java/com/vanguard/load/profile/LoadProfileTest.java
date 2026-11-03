package com.vanguard.load.profile;

import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LoadProfileTest {

    private final LoadProfile big = new LoadProfile("browse", "products", 50, 30, 120, 200);

    @Test
    public void smokeModeCapsUsersRampAndDuration() {
        LoadProfile smoke = big.limitedTo("smoke");

        assertThat(smoke.users()).isEqualTo(LoadProfile.SMOKE_MAX_USERS);
        assertThat(smoke.rampUpSeconds()).isEqualTo(LoadProfile.SMOKE_MAX_RAMP_SECONDS);
        assertThat(smoke.durationSeconds()).isEqualTo(LoadProfile.SMOKE_MAX_DURATION_SECONDS);
        assertThat(smoke.thinkTimeMs()).as("think time is never capped").isEqualTo(200);
    }

    @Test
    public void fullModeKeepsTheTableValues() {
        assertThat(big.limitedTo("full")).isEqualTo(big);
    }

    @Test
    public void missingTableColumnsUseDefaults() {
        LoadProfile profile = LoadProfile.fromTableRow("health", "health", Map.of("users", "3"));

        assertThat(profile).isEqualTo(new LoadProfile("health", "health", 3, 10, 30, 200));
    }

    @Test
    public void invalidValuesAreRejected() {
        assertThatThrownBy(() -> new LoadProfile("x", "health", 0, 1, 1, 0))
                .hasMessageContaining("users >= 1");
    }

    @Test
    public void unknownEndpointNamesListTheKnownOnes() {
        assertThatThrownBy(() -> LoadEndpoints.pathFor("nope"))
                .hasMessageContaining("Unknown load endpoint 'nope'")
                .hasMessageContaining("Add it to LoadEndpoints");
    }
}
