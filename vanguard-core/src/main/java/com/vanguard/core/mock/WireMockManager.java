package com.vanguard.core.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import lombok.extern.slf4j.Slf4j;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Runs ONE WireMock server per JVM on a free port.
 *
 * <p>Stubs live in {@code vanguard-core/src/main/resources/wiremock/mappings} (request → response rules)
 * and {@code wiremock/__files} (response bodies). Add a JSON file there to add a stub; no Java needed.
 */
@Slf4j
public final class WireMockManager {

    private static volatile WireMockServer server;

    private WireMockManager() {
    }

    /**
     * Starts the server if needed and returns its base URL, e.g. {@code http://localhost:51234}.
     */
    public static String start() {
        WireMockServer local = server;
        if (local == null) {
            synchronized (WireMockManager.class) {
                local = server;
                if (local == null) {
                    local = new WireMockServer(options()
                            .dynamicPort()
                            .usingFilesUnderClasspath("wiremock")
                            .notifier(new Slf4jNotifier(false)));
                    local.start();
                    server = local;
                    Runtime.getRuntime().addShutdownHook(new Thread(WireMockManager::stop, "wiremock-stop"));
                    log.info("WireMock started at {}", local.baseUrl());
                }
            }
        }
        return local.baseUrl();
    }

    /**
     * Stops the server. Safe to call more than once.
     */
    public static synchronized void stop() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }
}
