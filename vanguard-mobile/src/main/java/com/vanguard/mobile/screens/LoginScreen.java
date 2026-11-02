package com.vanguard.mobile.screens;

/**
 * REFERENCE EXAMPLE of a Screen Object: ONE class for Android AND iOS.
 * Locators at the top, one public method per user action.
 */
public class LoginScreen extends BaseScreen {

    // Demo credentials published by the sample app (src/utils/Constants.ts) - not a secret.
    public static final String STANDARD_USER = "bob@example.com";
    public static final String LOCKED_OUT_USER = "alice@example.com";
    private static final String DEMO_PASSWORD = "10203040";

    private static final MobileLocator SCREEN = MobileLocator.accessibilityId("login screen");
    private static final MobileLocator USERNAME = MobileLocator.accessibilityId("Username input field");
    private static final MobileLocator PASSWORD = MobileLocator.accessibilityId("Password input field");
    private static final MobileLocator LOGIN_BUTTON = MobileLocator.accessibilityId("Login button");
    private static final MobileLocator ERROR = MobileLocator.accessibilityId("generic-error-message");

    public void loginAs(String username) {
        waitUntilVisible(SCREEN);
        type(USERNAME, username);
        type(PASSWORD, DEMO_PASSWORD);
        hideKeyboard();
        tap(LOGIN_BUTTON);
    }

    public String errorMessage() {
        return getText(ERROR);
    }
}
