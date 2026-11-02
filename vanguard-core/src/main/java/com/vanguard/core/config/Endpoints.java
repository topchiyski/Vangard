package com.vanguard.core.config;

import com.vanguard.core.mock.WireMockManager;

/**
 * Resolves base URLs. An empty URL in config means "use the embedded WireMock server",
 * so sample tests always work offline.
 */
public final class Endpoints {

    private Endpoints() {
    }

    /** Base URL for API tests. */
    public static String apiBaseUrl() {
        return orWireMock(ConfigProvider.get().apiBaseUrl());
    }

    /** Base URL for load tests. */
    public static String loadBaseUrl() {
        return orWireMock(ConfigProvider.get().loadBaseUrl());
    }

    /** Base URL for the web UI. */
    public static String uiBaseUrl() {
        return stripTrailingSlash(ConfigProvider.get().uiBaseUrl());
    }

    private static String orWireMock(String configured) {
        if (configured == null || configured.isBlank()) {
            return WireMockManager.start();
        }
        return stripTrailingSlash(configured);
    }

    private static String stripTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
