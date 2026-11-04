package com.vanguard.core.visual;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Shared behavior for visual checks across web and mobile integrations.
 */
@Slf4j
public final class VisualTestSupport {

    private VisualTestSupport() {
    }

    /**
     * Logs why a checkpoint is skipped when visual checks are not configured.
     */
    public static boolean shouldSkipCheckpoint(String checkpointName) {
        Optional<String> skip = VisualSettings.skipReason();
        if (skip.isPresent()) {
            log.warn("Visual check skipped: {} [{}]", skip.get(), checkpointName);
            return true;
        }
        return false;
    }

    /**
     * Applies the shared outcome policy to an Applitools result.
     */
    public static void reportResult(boolean isNew, boolean isPassed, String status, String url,
                                    String reviewInstructions) {
        if (isNew) {
            log.info("New visual baseline saved - review it once in Applitools: {}", url);
        } else if (!isPassed) {
            throw new AssertionError("Visual differences found (" + status + "). "
                    + reviewInstructions + ": " + url);
        }
    }
}
