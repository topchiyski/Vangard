package com.vanguard.mobile.visual;

import com.applitools.ICheckSettings;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.vanguard.core.config.ConfigProvider;
import com.vanguard.core.driver.DriverRegistry;
import com.vanguard.core.visual.VisualSettings;
import com.vanguard.core.visual.VisualTestSupport;
import lombok.extern.slf4j.Slf4j;

/**
 * Applitools Eyes for NATIVE mobile screens (Classic runner: checks what the device shows).
 * Same rules as web: skipped with a warning without {@code -Dvisual.enabled=true} + {@code APPLITOOLS_API_KEY};
 * new baselines pass; differences fail the scenario.
 */
@Slf4j
public final class MobileEyesManager {

    private static final ThreadLocal<Eyes> EYES = new ThreadLocal<>();
    private static final ThreadLocal<String> TEST_NAME = new ThreadLocal<>();
    private static final ClassicRunner RUNNER = new ClassicRunner();
    private static volatile BatchInfo batch;

    private MobileEyesManager() {
    }

    /**
     * Remembers the scenario name; Applitools opens lazily at the first check.
     */
    public static void startScenario(String scenarioName) {
        TEST_NAME.set(scenarioName);
    }

    /**
     * Takes a visual checkpoint, or logs a warning and skips when visual checks are off.
     */
    public static void check(String checkpointName, ICheckSettings settings) {
        if (VisualTestSupport.shouldSkipCheckpoint(checkpointName)) {
            return;
        }
        eyes().check(checkpointName, settings);
    }

    /**
     * Closes the Applitools test of this scenario and fails it when visual differences were found.
     */
    public static void finishScenario() {
        Eyes eyes = EYES.get();
        TEST_NAME.remove();
        if (eyes == null) {
            return;
        }
        EYES.remove();
        try {
            TestResults results = eyes.close(false);
            if (results == null) {
                return;
            }
            VisualTestSupport.reportResult(Boolean.TRUE.equals(results.isNew()), results.isPassed(),
                    String.valueOf(results.getStatus()), results.getUrl(), "Review them in Applitools");
        } finally {
            eyes.abortIfNotClosed();
        }
    }

    private static Eyes eyes() {
        Eyes eyes = EYES.get();
        if (eyes == null) {
            eyes = new Eyes(RUNNER);
            Configuration config = eyes.getConfiguration();
            config.setApiKey(VisualSettings.apiKey());
            config.setBatch(batch());
            config.setAppName("Vanguard Mobile");
            config.setTestName(TEST_NAME.get() == null ? "Unnamed scenario" : TEST_NAME.get());
            eyes.setConfiguration(config);
            eyes.open(DriverRegistry.get(), "Vanguard Mobile", config.getTestName());
            EYES.set(eyes);
        }
        return eyes;
    }

    private static synchronized BatchInfo batch() {
        if (batch == null) {
            batch = new BatchInfo(ConfigProvider.get().visualBatchName());
        }
        return batch;
    }
}
