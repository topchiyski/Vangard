package com.vanguard.core.driver;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

/**
 * Holds the browser or mobile driver of the CURRENT thread (one scenario = one thread at a time).
 *
 * <p>The web and mobile modules register a factory in a {@code @Before} hook; the driver itself is only
 * created the first time a page or screen object asks for it. API and load scenarios therefore never
 * start a browser. Juniors never call this class directly: {@code BasePage} and {@code BaseScreen} do.
 */
@Slf4j
public final class DriverRegistry {

    private static final ThreadLocal<Supplier<? extends WebDriver>> FACTORY = new ThreadLocal<>();
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverRegistry() {
    }

    /** Registers how to create the driver for the current scenario. Called by module hooks. */
    public static void registerFactory(Supplier<? extends WebDriver> factory) {
        FACTORY.set(factory);
    }

    /** Returns the current driver, creating it on first use. */
    public static WebDriver get() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            Supplier<? extends WebDriver> factory = FACTORY.get();
            if (factory == null) {
                throw new IllegalStateException("No driver factory registered for this scenario. "
                        + "Tag the scenario with @web or @mobile so the right hooks start a driver.");
            }
            driver = factory.get();
            DRIVER.set(driver);
        }
        return driver;
    }

    /** True when a driver was created for the current scenario. */
    public static boolean isActive() {
        return DRIVER.get() != null;
    }

    /** Quits the driver (if any) and clears the thread. Called by the shared {@code @After} hook. */
    public static void quit() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (RuntimeException e) {
            log.warn("Driver did not quit cleanly: {}", e.getMessage());
        } finally {
            DRIVER.remove();
            FACTORY.remove();
        }
    }
}
