package com.vanguard.mobile.driver;

import com.vanguard.core.config.ConfigProvider;
import com.vanguard.core.config.FrameworkConfig;
import io.appium.java_client.AppiumClientConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds the Appium driver for a mobile scenario: Android (UiAutomator2) or iOS (XCUITest),
 * native/hybrid app or mobile browser, on a local device/emulator or on BrowserStack.
 * Juniors never call this directly: {@code MobileHooks} registers it for every {@code @mobile} scenario.
 */
public final class AppiumDriverFactory {

    private static final String BROWSERSTACK_HUB = "https://hub-cloud.browserstack.com/wd/hub";

    private AppiumDriverFactory() {
    }

    /**
     * Creates an Appium driver on the local Appium server or on BrowserStack ({@code mobile.target}).
     */
    public static WebDriver create(MobilePlatform platform, AppType appType, String scenarioName) {
        FrameworkConfig config = ConfigProvider.get();
        boolean cloud = "browserstack".equalsIgnoreCase(config.mobileTarget());
        URL server = cloud ? toUrl(BROWSERSTACK_HUB) : AppiumServerManager.ensureRunning();

        MutableCapabilities options = platform == MobilePlatform.ANDROID
                ? androidOptions(config, appType, cloud)
                : iosOptions(config, appType, cloud);
        if (cloud) {
            options.setCapability("bstack:options", browserStackOptions(config, platform, scenarioName));
        }

        // WHY: same h2c-upgrade hang as chromedriver (see ChromeDriverFactory) - force HTTP/1.1.
        AppiumClientConfig clientConfig = AppiumClientConfig.defaultConfig()
                .baseUrl(server)
                .version("HTTP_1_1")
                .readTimeout(Duration.ofMinutes(5));
        return platform == MobilePlatform.ANDROID
                ? new AndroidDriver(clientConfig, options)
                : new IOSDriver(clientConfig, options);
    }

    private static UiAutomator2Options androidOptions(FrameworkConfig config, AppType appType, boolean cloud) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(config.androidDevice())
                .setAutoGrantPermissions(true)
                .setNewCommandTimeout(Duration.ofSeconds(120));
        if (!config.androidVersion().isBlank()) {
            options.setPlatformVersion(config.androidVersion());
        }
        if (appType.installsApp()) {
            options.setApp(app(config.androidApp(), cloud, config));
        } else {
            options.withBrowserName("Chrome");
        }
        return options;
    }

    private static XCUITestOptions iosOptions(FrameworkConfig config, AppType appType, boolean cloud) {
        XCUITestOptions options = new XCUITestOptions()
                .setDeviceName(config.iosDevice())
                .setPlatformVersion(config.iosVersion())
                .setNewCommandTimeout(Duration.ofSeconds(120))
                .setAutoAcceptAlerts(true);
        if (appType.installsApp()) {
            options.setApp(app(config.iosApp(), cloud, config));
        } else {
            options.withBrowserName("Safari");
        }
        return options;
    }

    /**
     * A local file path, or a bs:// id (uploading the local file to BrowserStack when needed).
     */
    private static String app(String configured, boolean cloud, FrameworkConfig config) {
        if (BrowserStackAppUploader.isBrowserStackId(configured)) {
            return configured;
        }
        if (!BrowserStackAppUploader.exists(configured)) {
            throw new IllegalStateException("App file not found: " + Path.of(configured).toAbsolutePath()
                    + ". Download the sample apps with scripts/download-sample-apps (.ps1 on Windows, .sh on Mac/Linux) "
                    + "or set -Dmobile.android.app / -Dmobile.ios.app.");
        }
        if (cloud) {
            return BrowserStackAppUploader.upload(Path.of(configured), username(config), accessKey(config));
        }
        return Path.of(configured).toAbsolutePath().toString();
    }

    private static Map<String, Object> browserStackOptions(FrameworkConfig config, MobilePlatform platform,
                                                           String scenarioName) {
        Map<String, Object> bstack = new HashMap<>();
        bstack.put("userName", username(config));
        bstack.put("accessKey", accessKey(config));
        bstack.put("buildName", config.browserstackBuildName());
        bstack.put("sessionName", scenarioName);
        bstack.put("deviceName", platform == MobilePlatform.ANDROID ? config.androidDevice() : config.iosDevice());
        String version = platform == MobilePlatform.ANDROID ? config.androidVersion() : config.iosVersion();
        if (!version.isBlank()) {
            bstack.put("osVersion", version);
        }
        return bstack;
    }

    private static String username(FrameworkConfig config) {
        return requireSecret(ConfigProvider.secret(config.browserstackUsername(), "BROWSERSTACK_USERNAME"),
                "BROWSERSTACK_USERNAME");
    }

    private static String accessKey(FrameworkConfig config) {
        return requireSecret(ConfigProvider.secret(config.browserstackAccessKey(), "BROWSERSTACK_ACCESS_KEY"),
                "BROWSERSTACK_ACCESS_KEY");
    }

    private static String requireSecret(String value, String envName) {
        if (value.isBlank()) {
            throw new IllegalStateException(envName + " is not set. See docs/MOBILE_SETUP.md#browserstack");
        }
        return value;
    }

    private static URL toUrl(String url) {
        try {
            return URI.create(url).toURL();
        } catch (java.net.MalformedURLException e) {
            throw new IllegalArgumentException(url, e);
        }
    }
}
