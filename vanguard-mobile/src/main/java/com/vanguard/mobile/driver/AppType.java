package com.vanguard.mobile.driver;

import java.util.Collection;

/**
 * What runs on the phone:
 * {@code @native} = installed app, {@code @hybrid} = installed app with web views,
 * {@code @mobileweb} = a website in the phone's browser (Chrome on Android, Safari on iOS).
 */
public enum AppType {
    NATIVE, HYBRID, MOBILE_WEB;

    /**
     * The app type from the scenario tags: {@code @mobileweb}, {@code @hybrid}, otherwise native.
     */
    public static AppType resolve(Collection<String> scenarioTags) {
        if (scenarioTags.contains("@mobileweb")) {
            return MOBILE_WEB;
        }
        return scenarioTags.contains("@hybrid") ? HYBRID : NATIVE;
    }

    /**
     * True when the scenario needs the app installed (native and hybrid).
     */
    public boolean installsApp() {
        return this != MOBILE_WEB;
    }
}
