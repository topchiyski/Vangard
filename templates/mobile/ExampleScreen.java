package com.vanguard.templates.mobile;

import com.vanguard.mobile.screens.BaseScreen;
import com.vanguard.mobile.screens.MobileLocator;
import io.appium.java_client.AppiumBy;

/**
 * TEMPLATE: Screen Object (works on Android AND iOS).
 * Copy to vanguard-mobile/src/main/java/com/vanguard/mobile/screens/<Name>Screen.java and:
 * 1. TODO rename (e.g. SettingsScreen); package com.vanguard.mobile.screens
 * 2. TODO find accessibility ids with Appium Inspector (docs/MOBILE_SETUP.md#appium-inspector)
 * 3. TODO same id on both platforms -> MobileLocator.accessibilityId(...); different -> new MobileLocator(android, ios)
 */
public class ExampleScreen extends BaseScreen {

    private static final MobileLocator SCREEN = MobileLocator.accessibilityId("TODO screen");
    private static final MobileLocator SAVE = MobileLocator.accessibilityId("TODO Save button");
    private static final MobileLocator TITLE = new MobileLocator(
            AppiumBy.id("TODO.package:id/title"),          // Android
            AppiumBy.accessibilityId("TODO title"));       // iOS

    /**
     * Waits for the screen, then taps Save.
     */
    public void save() {
        waitUntilVisible(SCREEN);
        tap(SAVE);
    }

    /**
     * The title shown on the screen.
     */
    public String title() {
        return getText(TITLE);
    }
}
