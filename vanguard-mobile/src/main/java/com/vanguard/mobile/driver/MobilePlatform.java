package com.vanguard.mobile.driver;

import com.vanguard.core.config.ConfigProvider;

import java.util.Collection;

/** Android or iOS. */
public enum MobilePlatform {
    ANDROID, IOS;

    /** Priority: tag {@code @android} / {@code @ios}, then {@code -Dmobile.platform}, then the default (android). */
    public static MobilePlatform resolve(Collection<String> scenarioTags) {
        if (scenarioTags.contains("@ios")) {
            return IOS;
        }
        if (scenarioTags.contains("@android")) {
            return ANDROID;
        }
        return "ios".equalsIgnoreCase(ConfigProvider.get().mobilePlatform()) ? IOS : ANDROID;
    }
}
