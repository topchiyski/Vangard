package com.vanguard.templates.web;

import com.vanguard.web.pages.BasePage;
import org.openqa.selenium.By;

/**
 * TEMPLATE: Page Object.
 * Copy to vanguard-web/src/main/java/com/vanguard/web/pages/<YourPage>.java and:
 * 1. TODO rename the class (e.g. ProfilePage) and change the package to com.vanguard.web.pages
 * 2. TODO replace the locators (prefer id or data-test attributes; find them with Chrome DevTools)
 * 3. TODO add one public method per user action; return data, never WebElements
 */
public class ExamplePage extends BasePage {

    // TODO locators: private, static, final, UPPER_CASE names
    private static final By NAME_FIELD = By.id("TODO-name");
    private static final By SAVE_BUTTON = By.cssSelector("[data-test='TODO-save']");
    private static final By SUCCESS_MESSAGE = By.cssSelector("[data-test='TODO-message']");

    /** TODO describe what the user does. */
    public ExamplePage open() {
        open("/TODO-path");
        return this;
    }

    /** TODO one method per action the user performs. */
    public void saveName(String name) {
        type(NAME_FIELD, name);
        click(SAVE_BUTTON);
    }

    /** TODO methods that read something return plain data (String, int, BigDecimal, List...). */
    public String successMessage() {
        return getText(SUCCESS_MESSAGE);
    }
}
