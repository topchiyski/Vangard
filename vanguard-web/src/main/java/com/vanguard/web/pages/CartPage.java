package com.vanguard.web.pages;

import org.openqa.selenium.By;

import java.util.List;

/** The shopping cart (/cart.html). */
public class CartPage extends BasePage {

    private static final By ITEM_NAMES = By.cssSelector("[data-test='inventory-item-name']");
    private static final By CHECKOUT = By.id("checkout");

    public List<String> itemNames() {
        waitForUrlContains("cart.html");
        return getTexts(ITEM_NAMES);
    }

    public void checkout() {
        click(CHECKOUT);
    }
}
