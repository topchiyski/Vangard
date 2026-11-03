package com.vanguard.core.hooks;

import com.vanguard.core.context.ContextKey;
import com.vanguard.core.context.ScenarioContext;
import com.vanguard.core.driver.DriverRegistry;
import com.vanguard.core.mock.WireMockManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Hooks shared by EVERY module. Juniors never need to touch this class.
 *
 * <p>Order of {@code @After} hooks: higher {@code order} runs first.
 * 10_000 = failure evidence, 5_000 = module hooks (e.g. close Applitools), 0 = quit driver.
 */
@Slf4j
public class CoreHooks {

    public static final int AFTER_EVIDENCE = 10_000;
    public static final int AFTER_MODULE = 5_000;
    public static final int AFTER_QUIT = 0;

    private final ScenarioContext context;

    public CoreHooks(ScenarioContext context) {
        this.context = context;
    }

    /**
     * Starts the WireMock fake API once, before the first scenario.
     */
    @BeforeAll
    public static void startMockServer() {
        WireMockManager.start();
    }

    @Before(order = 0)
    public void rememberScenario(Scenario scenario) {
        context.set(ContextKey.SCENARIO_NAME, scenario.getName());
        log.info("▶ Scenario: {} {}", scenario.getName(), scenario.getSourceTagNames());
    }

    @After(order = AFTER_EVIDENCE)
    public void attachFailureEvidence(Scenario scenario) {
        if (!scenario.isFailed() || !DriverRegistry.isActive()) {
            return;
        }
        WebDriver driver = DriverRegistry.get();
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(png, "image/png", "Screenshot on failure");
        } catch (RuntimeException e) {
            log.warn("Could not take screenshot: {}", e.getMessage());
        }
        attachLocation(scenario, driver);
        attachBrowserConsole(scenario, driver);
    }

    @After(order = AFTER_QUIT)
    public void quitDriver(Scenario scenario) {
        DriverRegistry.quit();
        log.info("■ Scenario {}: {}", scenario.getStatus(), scenario.getName());
    }

    private void attachLocation(Scenario scenario, WebDriver driver) {
        try {
            scenario.attach(driver.getCurrentUrl(), "text/plain", "Current URL");
        } catch (RuntimeException notAWebContext) {
            // WHY: native mobile apps have no URL; the page source (UI tree) is the useful evidence there.
            try {
                scenario.attach(driver.getPageSource().getBytes(StandardCharsets.UTF_8), "text/xml", "Screen source");
            } catch (RuntimeException e) {
                log.warn("Could not capture page source: {}", e.getMessage());
            }
        }
    }

    private void attachBrowserConsole(Scenario scenario, WebDriver driver) {
        try {
            String logs = driver.manage().logs().get(LogType.BROWSER).getAll().stream()
                    .map(LogEntry::toString)
                    .collect(Collectors.joining(System.lineSeparator()));
            if (!logs.isBlank()) {
                scenario.attach(logs, "text/plain", "Browser console");
            }
        } catch (RuntimeException notSupported) {
            log.debug("Browser console logs not available for this driver");
        }
    }
}
