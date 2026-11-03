package com.vanguard.mobile.screens;

import io.appium.java_client.AppiumBy;

/**
 * The product catalog (start screen of the app).
 */
public class ProductsScreen extends BaseScreen {

    private static final MobileLocator SCREEN = MobileLocator.accessibilityId("products screen");
    private static final MobileLocator PRODUCT_NAMES = MobileLocator.accessibilityId("store item text");
    private static final MobileLocator CART_BADGE_TEXT = new MobileLocator(
            AppiumBy.xpath("//*[@content-desc='cart badge']/android.widget.TextView"),
            AppiumBy.xpath("//XCUIElementTypeButton[@name='tab bar option cart']//XCUIElementTypeStaticText"));
    private static final MobileLocator CART = new MobileLocator(
            AppiumBy.accessibilityId("cart badge"),
            AppiumBy.accessibilityId("tab bar option cart"));

    /**
     * Waits until the product list is shown.
     */
    public ProductsScreen waitUntilShown() {
        waitUntilVisible(SCREEN);
        return this;
    }

    /**
     * Opens a product by its visible name, scrolling down if needed.
     */
    public void openProduct(String productName) {
        MobileLocator product = productNamed(productName);
        scrollTo(product);
        tap(product);
    }

    /**
     * Scrolls until the product is visible; returns true when found.
     */
    public boolean scrollToProduct(String productName) {
        scrollTo(productNamed(productName));
        return isDisplayed(productNamed(productName));
    }

    /**
     * Names of the products visible right now.
     */
    public java.util.List<String> visibleProductNames() {
        waitUntilShown();
        return getTextsNow(PRODUCT_NAMES);
    }

    /**
     * Number on the cart icon; 0 when there is no badge.
     */
    public int cartCount() {
        return getTextsNow(CART_BADGE_TEXT).stream()
                .filter(t -> t.matches("\\d+"))
                .map(Integer::parseInt)
                .findFirst()
                .orElse(0);
    }

    /**
     * Opens the cart.
     */
    public void openCart() {
        tap(CART);
    }

    private static MobileLocator productNamed(String name) {
        return new MobileLocator(
                AppiumBy.androidUIAutomator("new UiSelector().description(\"store item text\").text(\"" + name + "\")"),
                AppiumBy.iOSNsPredicateString("name == 'store item text' AND label == '" + name + "'"));
    }
}
