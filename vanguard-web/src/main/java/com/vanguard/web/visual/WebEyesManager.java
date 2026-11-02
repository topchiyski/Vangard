package com.vanguard.web.visual;

import com.applitools.ICheckSettings;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.visualgrid.model.ChromeEmulationInfo;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.vanguard.core.config.ConfigProvider;
import com.vanguard.core.driver.DriverRegistry;
import com.vanguard.core.visual.VisualSettings;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Applitools Eyes for web scenarios. Juniors only use the visual steps
 * ("Then the "Inventory" page matches its visual baseline"); this class does the rest.
 *
 * <ul>
 *   <li>One runner for the whole run: Ultrafast Grid ({@code visual.runner=ufg}, renders desktop Chrome
 *       AND a phone in the cloud from one local run) or Classic ({@code visual.runner=classic}).</li>
 *   <li>One Eyes per scenario thread, opened lazily on the first check.</li>
 *   <li>Without {@code -Dvisual.enabled=true} + {@code APPLITOOLS_API_KEY} checks are skipped with a warning.</li>
 * </ul>
 */
@Slf4j
public final class WebEyesManager {

    private static final ThreadLocal<Eyes> EYES = new ThreadLocal<>();
    private static final ThreadLocal<String> TEST_NAME = new ThreadLocal<>();
    private static volatile EyesRunner runner;
    private static volatile BatchInfo batch;

    private WebEyesManager() {
    }

    /** Called by hooks before a {@code @visual} scenario. */
    public static void startScenario(String scenarioName) {
        TEST_NAME.set(scenarioName);
    }

    /** Takes a visual checkpoint, or logs why it was skipped. */
    public static void check(String checkpointName, ICheckSettings settings) {
        Optional<String> skip = VisualSettings.skipReason();
        if (skip.isPresent()) {
            log.warn("Visual check skipped: {} [{}]", skip.get(), checkpointName);
            return;
        }
        eyes().check(checkpointName, settings);
    }

    /**
     * Called by hooks after a {@code @visual} scenario. New baselines pass; visual differences fail the scenario.
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
            if (Boolean.TRUE.equals(results.isNew())) {
                log.info("New visual baseline saved - review it once in Applitools: {}", results.getUrl());
            } else if (!results.isPassed()) {
                throw new AssertionError("Visual differences found (" + results.getStatus() + "). "
                        + "Review and accept/reject them in Applitools: " + results.getUrl());
            }
        } finally {
            eyes.abortIfNotClosed();
        }
    }

    private static Eyes eyes() {
        Eyes eyes = EYES.get();
        if (eyes == null) {
            eyes = new Eyes(runner());
            Configuration config = eyes.getConfiguration();
            config.setApiKey(VisualSettings.apiKey());
            config.setBatch(batch());
            config.setAppName("Vanguard");
            config.setTestName(TEST_NAME.get() == null ? "Unnamed scenario" : TEST_NAME.get());
            if (isUltrafastGrid()) {
                config.addBrowser(1920, 1080, BrowserType.CHROME);
                // WHY: the Ultrafast Grid device list has no Pixel 7; Pixel 5 has the same 393-412px class width.
                config.addBrowser(new ChromeEmulationInfo(DeviceName.Pixel_5, ScreenOrientation.PORTRAIT));
            }
            eyes.setConfiguration(config);
            eyes.open(DriverRegistry.get());
            EYES.set(eyes);
        }
        return eyes;
    }

    private static boolean isUltrafastGrid() {
        return !"classic".equalsIgnoreCase(ConfigProvider.get().visualRunner());
    }

    private static synchronized EyesRunner runner() {
        if (runner == null) {
            runner = isUltrafastGrid() ? new VisualGridRunner(new RunnerOptions().testConcurrency(5)) : new ClassicRunner();
        }
        return runner;
    }

    private static synchronized BatchInfo batch() {
        if (batch == null) {
            batch = new BatchInfo(ConfigProvider.get().visualBatchName());
        }
        return batch;
    }
}
