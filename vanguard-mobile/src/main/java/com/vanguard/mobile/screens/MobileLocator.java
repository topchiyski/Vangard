package com.vanguard.mobile.screens;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * One locator for BOTH platforms. Most elements share an accessibility id, so use the shortcut:
 *
 * <pre>{@code
 * private static final MobileLocator LOGIN_BUTTON = MobileLocator.accessibilityId("Login button");
 * }</pre>
 *
 * When Android and iOS differ, give both:
 *
 * <pre>{@code
 * private static final MobileLocator CART = new MobileLocator(
 *         AppiumBy.accessibilityId("cart badge"),            // Android
 *         AppiumBy.accessibilityId("tab bar option cart"));  // iOS
 * }</pre>
 *
 * Find ids with Appium Inspector (docs/MOBILE_SETUP.md#appium-inspector).
 */
public record MobileLocator(By android, By ios) {

    /**
     * Same accessibility id on Android (content-desc) and iOS (accessibilityIdentifier / name).
     */
    public static MobileLocator accessibilityId(String id) {
        By by = AppiumBy.accessibilityId(id);
        return new MobileLocator(by, by);
    }

    /**
     * Same locator on both platforms (e.g. a CSS selector inside a web view).
     */
    public static MobileLocator both(By by) {
        return new MobileLocator(by, by);
    }
}
