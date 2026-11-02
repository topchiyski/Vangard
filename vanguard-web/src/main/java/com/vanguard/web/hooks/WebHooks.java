package com.vanguard.web.hooks;

import com.vanguard.core.driver.DriverRegistry;
import com.vanguard.core.hooks.CoreHooks;
import com.vanguard.web.driver.BrowserMode;
import com.vanguard.web.driver.ChromeDriverFactory;
import com.vanguard.web.visual.WebEyesManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Web hooks. Every scenario tagged {@code @web} gets Chrome (created only when a page is first used).
 * Tag {@code @mobile-emulation} for a phone-sized Chrome, {@code @visual} for Applitools checks.
 */
public class WebHooks {

    @Before(value = "@web", order = 10)
    public void registerChrome(Scenario scenario) {
        BrowserMode mode = BrowserMode.resolve(scenario.getSourceTagNames());
        DriverRegistry.registerFactory(() -> ChromeDriverFactory.create(mode));
    }

    @Before(value = "@web and @visual", order = 20)
    public void startVisual(Scenario scenario) {
        WebEyesManager.startScenario(scenario.getName());
    }

    @After(value = "@web and @visual", order = CoreHooks.AFTER_MODULE)
    public void finishVisual() {
        WebEyesManager.finishScenario();
    }
}
