package com.vanguard.web.pages;

import com.vanguard.core.config.ConfigProvider;
import org.openqa.selenium.By;

/**
 * REFERENCE EXAMPLE of a Page Object: locators at the top (private), one public method per user action.
 * Steps call these methods; they never touch locators.
 */
public class LoginPage extends BasePage {

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    /**
     * Opens the login page.
     */
    public LoginPage open() {
        open("/");
        return this;
    }

    /**
     * Logs in with a SauceDemo demo user; the password comes from config (ui.password).
     */
    public void loginAs(String username) {
        type(USERNAME, username);
        type(PASSWORD, ConfigProvider.get().uiPassword());
        click(LOGIN_BUTTON);
    }

    /**
     * The red error banner text, e.g. "Epic sadface: ...".
     */
    public String errorMessage() {
        return getText(ERROR_MESSAGE);
    }
}
