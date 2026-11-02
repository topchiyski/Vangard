package com.vanguard.web.driver;

import com.vanguard.core.config.ConfigProvider;

import java.util.Collection;

/** Desktop Chrome or Chrome emulating a phone. */
public enum BrowserMode {
    DESKTOP, MOBILE_EMULATION;

    /**
     * Picks the mode for a scenario. Priority: tag {@code @mobile-emulation} / {@code @desktop},
     * then {@code -Dbrowser.mode=desktop|mobile}, then the default (desktop).
     */
    public static BrowserMode resolve(Collection<String> scenarioTags) {
        if (scenarioTags.contains("@mobile-emulation")) {
            return MOBILE_EMULATION;
        }
        if (scenarioTags.contains("@desktop")) {
            return DESKTOP;
        }
        return "mobile".equalsIgnoreCase(ConfigProvider.get().browserMode()) ? MOBILE_EMULATION : DESKTOP;
    }
}
