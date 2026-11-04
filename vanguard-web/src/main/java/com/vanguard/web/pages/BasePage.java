package com.vanguard.web.pages;

import com.vanguard.core.config.Endpoints;
import com.vanguard.core.driver.DriverRegistry;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Parent of every Page Object. All interactions wait up to 10 seconds automatically,
 * so you never need {@code Thread.sleep()} or {@code driver.findElement()}.
 *
 * <pre>{@code
 * public class LoginPage extends BasePage {
 *     private static final By USERNAME = By.id("user-name");
 *     public void enterUsername(String name) { type(USERNAME, name); }
 * }
 * }</pre>
 */
public abstract class BasePage {

    protected static final Duration TIMEOUT = Duration.ofSeconds(10);

    /**
     * Opens a path of the web app, e.g. {@code open("/inventory.html")}.
     */
    protected void open(String path) {
        driver().get(Endpoints.uiBaseUrl() + (path.startsWith("/") ? path : "/" + path));
    }

    /**
     * Waits until the element is clickable, then clicks it.
     */
    protected void click(By locator) {
        waitFor(ExpectedConditions.elementToBeClickable(locator), locator, "clickable").click();
    }

    /**
     * Waits for the field, clears it, then types the text.
     */
    protected void type(By locator, String text) {
        WebElement field = waitFor(ExpectedConditions.visibilityOfElementLocated(locator), locator, "visible");
        field.clear();
        field.sendKeys(text);
    }

    /**
     * Waits for the element and returns its visible text, trimmed.
     */
    protected String getText(By locator) {
        return waitFor(ExpectedConditions.visibilityOfElementLocated(locator), locator, "visible").getText().trim();
    }

    /**
     * Returns the visible text of every matching element (waits for at least one).
     */
    protected List<String> getTexts(By locator) {
        return waitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator), locator, "visible")
                .stream().map(e -> e.getText().trim()).toList();
    }

    /**
     * True if the element is visible right now. Never throws, never waits long.
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver().findElements(locator).stream().anyMatch(WebElement::isDisplayed);
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * True if the element becomes visible within the timeout; false (no exception) otherwise.
     */
    protected boolean appearsWithin(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver(), timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Waits until the element is gone or hidden (e.g. a closing menu or a spinner).
     */
    protected void waitUntilHidden(By locator) {
        waitFor(ExpectedConditions.invisibilityOfElementLocated(locator), locator, "hidden");
    }

    /**
     * Waits until the current URL contains the text.
     */
    protected void waitForUrlContains(String fraction) {
        try {
            new WebDriverWait(driver(), TIMEOUT).until(ExpectedConditions.urlContains(fraction));
        } catch (TimeoutException e) {
            throw new AssertionError(pageName() + ": URL did not contain '" + fraction + "' after "
                    + TIMEOUT.toSeconds() + "s. Current URL: " + currentUrl(), e);
        }
    }

    /**
     * The URL currently shown in the browser.
     */
    protected String currentUrl() {
        return driver().getCurrentUrl();
    }

    /**
     * The page title (the browser tab text).
     */
    protected String title() {
        return driver().getTitle();
    }

    /**
     * Runs JavaScript in the page, e.g. {@code executeScript("return window.innerWidth")}.
     */
    protected Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver()).executeScript(script, args);
    }

    /**
     * The current browser. Prefer the helpers above; use this only for things they don't cover.
     */
    protected WebDriver driver() {
        return DriverRegistry.get();
    }

    private <T> T waitFor(ExpectedCondition<T> condition, By locator, String state) {
        try {
            return new WebDriverWait(driver(), TIMEOUT).until(condition);
        } catch (TimeoutException e) {
            throw new AssertionError(pageName() + ": element " + locator + " not " + state + " after "
                    + TIMEOUT.toSeconds() + "s on " + currentUrl()
                    + " - check the locator in " + pageName() + " or whether the page loaded.", e);
        }
    }

    private String pageName() {
        return getClass().getSimpleName();
    }
}
