package com.vanguard.core.visual;

import com.vanguard.core.config.ConfigProvider;

import java.util.Optional;

/**
 * Decides whether Applitools visual checks really run.
 * Visual checks need BOTH {@code -Dvisual.enabled=true} AND the {@code APPLITOOLS_API_KEY} environment variable.
 */
public final class VisualSettings {

    private VisualSettings() {
    }

    /**
     * The Applitools API key from {@code APPLITOOLS_API_KEY}, or "" when it is not set.
     */
    public static String apiKey() {
        String key = System.getenv("APPLITOOLS_API_KEY");
        return key == null ? "" : key.trim();
    }

    /**
     * Empty when visual checks run; otherwise the reason they are skipped.
     */
    public static Optional<String> skipReason() {
        if (!ConfigProvider.get().visualEnabled()) {
            return Optional.of("visual.enabled=false (run with -Dvisual.enabled=true)");
        }
        if (apiKey().isEmpty()) {
            return Optional.of("APPLITOOLS_API_KEY environment variable is not set");
        }
        return Optional.empty();
    }

    /**
     * True when visual checks will really run (see {@link #skipReason()}).
     */
    public static boolean isEffectivelyEnabled() {
        return skipReason().isEmpty();
    }
}
