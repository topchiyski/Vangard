package com.vanguard.mobile.screens;

import io.appium.java_client.AppiumBy;

/**
 * The app menu. Android opens a drawer ("open menu"); iOS uses the "Menu" tab - a good example of a
 * {@link MobileLocator} with different Android and iOS locators. Menu entries share ids on both.
 * Ids come from the app source: saucelabs/my-demo-app-rn v1.3.0, src/config/translations/en.ts.
 */
public class MenuScreen extends BaseScreen {

    private static final MobileLocator OPEN_MENU = new MobileLocator(
            AppiumBy.accessibilityId("open menu"),             // Android: drawer button
            AppiumBy.accessibilityId("tab bar option menu"));  // iOS: tab bar
    private static final MobileLocator LOG_IN = MobileLocator.accessibilityId("menu item log in");
    private static final MobileLocator WEBVIEW = MobileLocator.accessibilityId("menu item webview");
    private static final MobileLocator CATALOG = MobileLocator.accessibilityId("menu item catalog");

    /**
     * Opens the menu and goes to the login screen.
     */
    public void openLogin() {
        tap(OPEN_MENU);
        tap(LOG_IN);
    }

    /**
     * Opens the menu and goes to the web view screen.
     */
    public void openWebView() {
        tap(OPEN_MENU);
        tap(WEBVIEW);
    }

    /**
     * Opens the menu and goes to the product catalog.
     */
    public void openCatalog() {
        tap(OPEN_MENU);
        tap(CATALOG);
    }
}
