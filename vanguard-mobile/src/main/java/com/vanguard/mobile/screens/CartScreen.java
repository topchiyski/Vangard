package com.vanguard.mobile.screens;

import java.util.List;

/** The shopping cart. */
public class CartScreen extends BaseScreen {

    private static final MobileLocator SCREEN = MobileLocator.accessibilityId("cart screen");
    private static final MobileLocator ITEM_NAMES = MobileLocator.accessibilityId("product label");

    public List<String> itemNames() {
        waitUntilVisible(SCREEN);
        return getTextsNow(ITEM_NAMES);
    }
}
