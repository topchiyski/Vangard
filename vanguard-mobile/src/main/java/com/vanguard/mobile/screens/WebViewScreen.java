package com.vanguard.mobile.screens;

/**
 * The app's built-in browser (hybrid example). Native part: type a URL and tap "Go To Site".
 * Web part: after {@link #switchToWebView()} the page is a normal web page - use CSS locators or even the
 * web module's Page Objects.
 */
public class WebViewScreen extends BaseScreen {

    private static final MobileLocator SCREEN = MobileLocator.accessibilityId("webview selection screen");
    private static final MobileLocator URL = MobileLocator.accessibilityId("URL input field");
    private static final MobileLocator GO = MobileLocator.accessibilityId("Go To Site button");

    public void openSite(String url) {
        waitUntilVisible(SCREEN);
        type(URL, url);
        hideKeyboard();
        tap(GO);
    }

    /** Title of the web page; call after {@link #switchToWebView()}. */
    public String webPageTitle() {
        return driver().getTitle();
    }
}
