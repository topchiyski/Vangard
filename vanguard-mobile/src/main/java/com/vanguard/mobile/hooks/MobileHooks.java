package com.vanguard.mobile.hooks;

import com.vanguard.core.config.ConfigProvider;
import com.vanguard.core.driver.DriverRegistry;
import com.vanguard.core.hooks.CoreHooks;
import com.vanguard.mobile.driver.AppType;
import com.vanguard.mobile.driver.AppiumDriverFactory;
import com.vanguard.mobile.driver.MobilePlatform;
import com.vanguard.mobile.visual.MobileEyesManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;

/**
 * Mobile hooks. Every {@code @mobile} scenario gets an Appium driver (created when a screen is first used).
 * Tags choose the platform ({@code @android} / {@code @ios}) and app type ({@code @native}, {@code @hybrid},
 * {@code @mobileweb}).
 */
@Slf4j
public class MobileHooks {

    @Before(value = "@mobile", order = 10)
    public void registerAppium(Scenario scenario) {
        MobilePlatform platform = MobilePlatform.resolve(scenario.getSourceTagNames());
        AppType appType = AppType.resolve(scenario.getSourceTagNames());
        DriverRegistry.registerFactory(() -> AppiumDriverFactory.create(platform, appType, scenario.getName()));
    }

    @Before(value = "@mobile and @visual", order = 20)
    public void startVisual(Scenario scenario) {
        MobileEyesManager.startScenario(scenario.getName());
    }

    @After(value = "@mobile and @visual", order = CoreHooks.AFTER_MODULE)
    public void finishVisual() {
        MobileEyesManager.finishScenario();
    }

    /**
     * Shows passed/failed in the BrowserStack dashboard.
     */
    @After(value = "@mobile", order = CoreHooks.AFTER_MODULE - 1)
    public void reportToBrowserStack(Scenario scenario) {
        if (!"browserstack".equalsIgnoreCase(ConfigProvider.get().mobileTarget()) || !DriverRegistry.isActive()) {
            return;
        }
        String status = scenario.isFailed() ? "failed" : "passed";
        try {
            ((JavascriptExecutor) DriverRegistry.get()).executeScript("browserstack_executor: "
                    + "{\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"" + status + "\"}}");
        } catch (RuntimeException e) {
            log.warn("Could not send the session status to BrowserStack: {}", e.getMessage());
        }
    }
}
