package com.vanguard.mobile.screens;

import java.math.BigDecimal;

/**
 * A single product with its price and "Add To Cart" button.
 */
public class ProductDetailsScreen extends BaseScreen {

    private static final MobileLocator SCREEN = MobileLocator.accessibilityId("product screen");
    private static final MobileLocator PRICE = MobileLocator.accessibilityId("product price");
    private static final MobileLocator ADD_TO_CART = MobileLocator.accessibilityId("Add To Cart button");

    /**
     * Scrolls to the Add To Cart button and taps it.
     */
    public void addToCart() {
        waitUntilVisible(SCREEN);
        scrollTo(ADD_TO_CART);
        tap(ADD_TO_CART);
    }

    /**
     * The price shown, e.g. 29.99.
     */
    public BigDecimal price() {
        return new BigDecimal(getText(PRICE).replace("$", "").trim());
    }
}
