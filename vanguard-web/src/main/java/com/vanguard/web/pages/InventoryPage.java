package com.vanguard.web.pages;

import org.openqa.selenium.By;

import java.math.BigDecimal;
import java.util.List;

/** The product list shown after login (/inventory.html). */
public class InventoryPage extends BasePage {

    /** Every price on the page. Public so visual checks can ignore prices (they change often). */
    public static final By PRICES = By.cssSelector(".inventory_item_price");

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By PRODUCT_NAMES = By.cssSelector("[data-test='inventory-item-name']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
    private static final By MENU_ITEMS = By.cssSelector(".bm-item-list a");
    private static final By CLOSE_MENU = By.id("react-burger-cross-btn");

    /** Waits until the inventory page is shown. */
    public InventoryPage waitUntilLoaded() {
        waitForUrlContains("inventory.html");
        return this;
    }

    public String title() {
        return getText(TITLE);
    }

    public List<String> productNames() {
        return getTexts(PRODUCT_NAMES);
    }

    /** Clicks "Add to cart" for the product with this exact name. */
    public void addToCart(String productName) {
        click(productButton(productName));
    }

    /** The price of a product as shown in the shop, e.g. 29.99. */
    public BigDecimal priceOf(String productName) {
        By price = By.xpath(productCard(productName) + "//*[@data-test='inventory-item-price']");
        return new BigDecimal(getText(price).replace("$", ""));
    }

    /** Number shown on the cart icon; 0 when the badge is hidden. */
    public int cartBadgeCount() {
        return isDisplayed(CART_BADGE) ? Integer.parseInt(getText(CART_BADGE)) : 0;
    }

    public void openCart() {
        click(CART_LINK);
    }

    /** Opens the burger menu, reads its entries (e.g. "All Items", "Logout") and closes it again. */
    public List<String> menuEntries() {
        click(MENU_BUTTON);
        // WHY: right after login the burger button can be clicked before its handler is attached,
        // so the menu stays shut (seen in parallel runs). One more click opens it reliably.
        if (!appearsWithin(MENU_ITEMS, java.time.Duration.ofSeconds(3))) {
            click(MENU_BUTTON);
        }
        List<String> entries = getTexts(MENU_ITEMS);
        click(CLOSE_MENU);
        // WHY: the menu slides out with an animation and would block clicks on the products meanwhile.
        waitUntilHidden(MENU_ITEMS);
        return entries;
    }

    /** Width of the browser viewport in CSS pixels. */
    public long viewportWidth() {
        return ((Number) executeScript("return window.innerWidth;")).longValue();
    }

    private static By productButton(String productName) {
        return By.xpath(productCard(productName) + "//button");
    }

    // WHY: locate the card by the product's visible name, so juniors pass names, not generated ids.
    private static String productCard(String productName) {
        return "//*[@data-test='inventory-item-name' and normalize-space()='" + productName + "']"
                + "/ancestor::*[@data-test='inventory-item']";
    }
}
