package com.vanguard.web.driver;

import com.vanguard.core.config.ConfigProvider;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.http.ClientConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds Chrome for web scenarios. Selenium Manager downloads the matching chromedriver automatically.
 * Juniors never call this directly: {@code WebHooks} registers it for every {@code @web} scenario.
 */
@Slf4j
public final class ChromeDriverFactory {

    /** Fallback metrics when Chrome does not know a device name. */
    private static final Map<String, Map<String, Object>> DEVICES = Map.of(
            "Pixel 7", device(412, 915, 2.625,
                    "Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) "
                            + "Chrome/120.0.0.0 Mobile Safari/537.36"),
            "iPhone 14 Pro Max", device(430, 932, 3.0,
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 "
                            + "(KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1"));

    private ChromeDriverFactory() {
    }

    public static WebDriver create(BrowserMode mode) {
        if (mode == BrowserMode.DESKTOP) {
            ChromeOptions options = baseOptions();
            options.addArguments("--window-size=1920,1080");
            return start(options);
        }
        String deviceName = ConfigProvider.get().emulationDeviceName();
        try {
            ChromeOptions options = baseOptions();
            options.setExperimentalOption("mobileEmulation", Map.of("deviceName", deviceName));
            return start(options);
        } catch (WebDriverException unknownDevice) {
            Map<String, Object> fallback = DEVICES.get(deviceName);
            if (fallback == null) {
                throw new IllegalStateException("Chrome does not know the device '" + deviceName
                        + "' and Vanguard has no fallback for it. Use one of " + DEVICES.keySet()
                        + " with -Demulation.device.name=...", unknownDevice);
            }
            log.warn("Chrome rejected device '{}', using built-in metrics instead", deviceName);
            ChromeOptions options = baseOptions();
            options.setExperimentalOption("mobileEmulation", fallback);
            return start(options);
        }
    }

    // WHY: Selenium 4.52's HTTP client sends an "Upgrade: h2c" header with POST /session; chromedriver
    // ignores ~30% of those requests and the session hangs until the 3-minute timeout. HTTP/1.1 fixes it.
    private static WebDriver start(ChromeOptions options) {
        return new ChromeDriver(ChromeDriverService.createDefaultService(), options,
                ClientConfig.defaultConfig().version("HTTP_1_1"));
    }

    private static ChromeOptions baseOptions() {
        ChromeOptions options = new ChromeOptions();
        if (ConfigProvider.get().headless()) {
            options.addArguments("--headless=new");
        }
        // WHY: needed on CI containers (small /dev/shm, no sandbox user).
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        // WHY: Chrome's password-leak popup appears after logging in to SauceDemo and blocks clicks.
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        // Lets hooks attach the browser console to failed scenarios.
        options.setCapability("goog:loggingPrefs", Map.of("browser", "ALL"));
        return options;
    }

    private static Map<String, Object> device(int width, int height, double pixelRatio, String userAgent) {
        return Map.of(
                "deviceMetrics", Map.of("width", width, "height", height, "pixelRatio", pixelRatio, "mobile", true),
                "userAgent", userAgent);
    }
}
