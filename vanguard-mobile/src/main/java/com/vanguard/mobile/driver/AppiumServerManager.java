package com.vanguard.mobile.driver;

import com.vanguard.core.config.ConfigProvider;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Makes sure a LOCAL Appium server is reachable: uses one that is already running at
 * {@code mobile.appium.url}, otherwise starts one (when {@code mobile.appium.autostart=true}).
 */
@Slf4j
public final class AppiumServerManager {

    private static final String SETUP_HELP = "See docs/MOBILE_SETUP.md#appium";
    private static volatile AppiumDriverLocalService service;

    private AppiumServerManager() {
    }

    /**
     * Returns the URL of a running Appium server, starting one if needed.
     */
    public static synchronized URL ensureRunning() {
        URI configured = URI.create(ConfigProvider.get().appiumUrl());
        if (isUp(configured)) {
            return toUrl(configured);
        }
        if (!ConfigProvider.get().appiumAutostart()) {
            throw new IllegalStateException("Appium not reachable at " + configured
                    + " - start it with 'appium' in a terminal, or set -Dmobile.appium.autostart=true. " + SETUP_HELP);
        }
        if (service == null || !service.isRunning()) {
            log.info("Starting a local Appium server on port {}", configured.getPort());
            try {
                service = new AppiumServiceBuilder()
                        .withIPAddress(configured.getHost())
                        .usingPort(configured.getPort())
                        .withTimeout(Duration.ofSeconds(60))
                        .build();
                service.start();
            } catch (RuntimeException e) {
                throw new IllegalStateException("Could not start Appium automatically. Is Node.js installed and "
                        + "'appium' on your PATH (npm i -g appium) with drivers installed "
                        + "(appium driver install uiautomator2 / xcuitest)? " + SETUP_HELP, e);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> service.stop(), "appium-stop"));
        }
        return toUrl(configured);
    }

    private static boolean isUp(URI server) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(3))
                    .build();
            HttpResponse<Void> response = client.send(
                    HttpRequest.newBuilder(server.resolve("/status")).timeout(Duration.ofSeconds(5)).GET().build(),
                    HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private static URL toUrl(URI uri) {
        try {
            return uri.toURL();
        } catch (java.net.MalformedURLException e) {
            throw new IllegalArgumentException("Invalid mobile.appium.url: " + uri, e);
        }
    }
}
