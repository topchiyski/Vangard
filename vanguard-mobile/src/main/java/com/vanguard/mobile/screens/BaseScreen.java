package com.vanguard.mobile.screens;

import com.vanguard.core.driver.DriverRegistry;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.SupportsContextSwitching;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Parent of every Screen Object (the mobile version of a Page Object). Works for Android AND iOS:
 * pass a {@link MobileLocator} and the right locator for the current platform is used.
 * Every interaction waits automatically - never use {@code Thread.sleep()}.
 */
public abstract class BaseScreen {

    protected static final Duration TIMEOUT = Duration.ofSeconds(15);
    private static final int MAX_SCROLLS = 6;

    /** Directions for {@link #swipe(Direction)}. UP moves the content up (= scroll down). */
    public enum Direction { UP, DOWN, LEFT, RIGHT }

    protected void tap(MobileLocator locator) {
        waitFor(locator, ExpectedConditions.elementToBeClickable(by(locator)), "tappable").click();
    }

    protected void type(MobileLocator locator, String text) {
        WebElement field = waitFor(locator, ExpectedConditions.visibilityOfElementLocated(by(locator)), "visible");
        field.clear();
        field.sendKeys(text);
    }

    protected String getText(MobileLocator locator) {
        return waitFor(locator, ExpectedConditions.visibilityOfElementLocated(by(locator)), "visible").getText().trim();
    }

    /** Texts of all matching elements right now (no waiting; empty list if none). */
    protected List<String> getTextsNow(MobileLocator locator) {
        return driver().findElements(by(locator)).stream().map(e -> e.getText().trim()).toList();
    }

    /** True if the element is visible right now. Never throws. */
    protected boolean isDisplayed(MobileLocator locator) {
        try {
            return driver().findElements(by(locator)).stream().anyMatch(WebElement::isDisplayed);
        } catch (RuntimeException e) {
            return false;
        }
    }

    /** Waits until the element is visible (e.g. to confirm a screen has opened). */
    protected void waitUntilVisible(MobileLocator locator) {
        waitFor(locator, ExpectedConditions.visibilityOfElementLocated(by(locator)), "visible");
    }

    /** Swipes across the middle of the screen with one finger (W3C actions; works on both platforms). */
    public void swipe(Direction direction) {
        Dimension size = driver().manage().window().getSize();
        int midX = size.width / 2;
        int midY = size.height / 2;
        int dx = size.width / 3;
        int dy = size.height / 3;
        int[] to = switch (direction) {
            case UP -> new int[]{midX, midY - dy};
            case DOWN -> new int[]{midX, midY + dy};
            case LEFT -> new int[]{midX - dx, midY};
            case RIGHT -> new int[]{midX + dx, midY};
        };
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), midX, midY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(400), PointerInput.Origin.viewport(), to[0], to[1]))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        appium().perform(List.of(swipe));
    }

    /** Scrolls down until the element is visible (max 6 swipes). */
    protected void scrollTo(MobileLocator locator) {
        for (int i = 0; i < MAX_SCROLLS && !isDisplayed(locator); i++) {
            swipe(Direction.UP);
        }
        waitUntilVisible(locator);
    }

    protected void hideKeyboard() {
        try {
            if (appium() instanceof AndroidDriver android) {
                android.hideKeyboard();
            } else {
                ((JavascriptExecutor) driver()).executeScript("mobile: hideKeyboard");
            }
        } catch (RuntimeException keyboardNotShown) {
            // Nothing to hide.
        }
    }

    /** Switches into the first web view (waits until the app has created it). */
    public void switchToWebView() {
        SupportsContextSwitching contexts = (SupportsContextSwitching) driver();
        String webView = new WebDriverWait(driver(), TIMEOUT).until(d -> {
            Set<String> handles = contexts.getContextHandles();
            return handles.stream().filter(h -> h.startsWith("WEBVIEW")).findFirst().orElse(null);
        });
        contexts.context(webView);
    }

    /** Switches back from a web view to the native app. */
    public void switchToNative() {
        ((SupportsContextSwitching) driver()).context("NATIVE_APP");
    }

    public void rotate(ScreenOrientation orientation) {
        ((io.appium.java_client.remote.SupportsRotation) driver()).rotate(orientation);
    }

    protected Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver()).executeScript(script, args);
    }

    protected boolean isAndroid() {
        return appium() instanceof AndroidDriver;
    }

    protected org.openqa.selenium.WebDriver driver() {
        return DriverRegistry.get();
    }

    private AppiumDriver appium() {
        return (AppiumDriver) driver();
    }

    private By by(MobileLocator locator) {
        return isAndroid() ? locator.android() : locator.ios();
    }

    private <T> T waitFor(MobileLocator locator, org.openqa.selenium.support.ui.ExpectedCondition<T> condition,
                          String state) {
        try {
            return new WebDriverWait(driver(), TIMEOUT).until(condition);
        } catch (TimeoutException e) {
            throw new AssertionError(getClass().getSimpleName() + ": element " + by(locator) + " not " + state
                    + " after " + TIMEOUT.toSeconds() + "s on " + (isAndroid() ? "Android" : "iOS")
                    + " - check the locator with Appium Inspector (docs/MOBILE_SETUP.md#appium-inspector).", e);
        }
    }
}
