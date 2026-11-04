package com.vanguard.web.pages;

import org.openqa.selenium.By;

import java.math.BigDecimal;

/**
 * Checkout: customer information, overview and confirmation steps.
 */
public class CheckoutPage extends BasePage {

    private static final By FIRST_NAME = By.id("first-name");
    private static final By LAST_NAME = By.id("last-name");
    private static final By POSTAL_CODE = By.id("postal-code");
    private static final By CONTINUE = By.id("continue");
    private static final By ITEM_TOTAL = By.cssSelector("[data-test='subtotal-label']");
    private static final By FINISH = By.id("finish");
    private static final By COMPLETE_HEADER = By.cssSelector("[data-test='complete-header']");

    /**
     * Fills "Your Information" and continues to the overview.
     */
    public void enterCustomer(String firstName, String lastName, String postalCode) {
        type(FIRST_NAME, firstName);
        type(LAST_NAME, lastName);
        type(POSTAL_CODE, postalCode);
        click(CONTINUE);
    }

    /**
     * The "Item total" on the overview, e.g. 29.99 (without tax).
     */
    public BigDecimal itemTotal() {
        String label = getText(ITEM_TOTAL); // "Item total: $29.99"
        return new BigDecimal(label.substring(label.indexOf('$') + 1));
    }

    /**
     * Places the order.
     */
    public void finish() {
        click(FINISH);
    }

    /**
     * The confirmation headline, e.g. "Thank you for your order!".
     */
    public String confirmation() {
        return getText(COMPLETE_HEADER);
    }
}
