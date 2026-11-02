package com.vanguard.mobile.steps;

import com.vanguard.web.pages.InventoryPage;
import com.vanguard.web.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Website in the phone's REAL browser (feature: mobile_web_saucedemo.feature).
 * Reuses the web module's Page Objects unchanged: they only need a WebDriver, and Appium is one.
 */
public class MobileWebSteps {

    private final LoginPage loginPage;
    private final InventoryPage inventory;

    public MobileWebSteps(LoginPage loginPage, InventoryPage inventory) {
        this.loginPage = loginPage;
        this.inventory = inventory;
    }

    @Given("I am logged in to the shop website as {string}")
    public void iAmLoggedInToTheShopWebsite(String username) {
        loginPage.open().loginAs(username);
        inventory.waitUntilLoaded();
    }

    @When("I add the {string} to the cart on the website")
    public void iAddToTheCartOnTheWebsite(String productName) {
        inventory.addToCart(productName);
    }

    @Then("the website cart badge shows {int}")
    public void theWebsiteCartBadgeShows(int expected) {
        assertThat(inventory.cartBadgeCount()).isEqualTo(expected);
    }
}
