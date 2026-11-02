package com.vanguard.web.steps;

import com.vanguard.web.pages.CartPage;
import com.vanguard.web.pages.CheckoutPage;
import com.vanguard.web.pages.InventoryPage;
import com.vanguard.web.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * REFERENCE EXAMPLE of web step definitions (features: web_saucedemo_e2e.feature, web_responsive.feature).
 *
 * <p>Pattern: each step is 1-3 lines that call Page Object methods, then assert with AssertJ.
 * No locators, no waits, no driver here: the Page Objects handle all of that.
 */
public class ShoppingSteps {

    private final LoginPage loginPage;
    private final InventoryPage inventory;
    private final CartPage cart;
    private final CheckoutPage checkout;

    // WHY: Cucumber (PicoContainer) creates the pages for us. Never write "new LoginPage()".
    public ShoppingSteps(LoginPage loginPage, InventoryPage inventory, CartPage cart, CheckoutPage checkout) {
        this.loginPage = loginPage;
        this.inventory = inventory;
        this.cart = cart;
        this.checkout = checkout;
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        loginPage.open();
    }

    @When("I log in as {string}")
    public void iLogInAs(String username) {
        loginPage.loginAs(username);
    }

    @Given("I am logged in as {string}")
    public void iAmLoggedInAs(String username) {
        loginPage.open().loginAs(username);
        inventory.waitUntilLoaded();
    }

    @When("I add the {string} to the cart")
    public void iAddTheProductToTheCart(String productName) {
        inventory.addToCart(productName);
    }

    @When("I open the cart")
    public void iOpenTheCart() {
        inventory.openCart();
    }

    @When("I check out as {string} {string} with postcode {string}")
    public void iCheckOutAs(String firstName, String lastName, String postcode) {
        inventory.openCart();
        cart.checkout();
        checkout.enterCustomer(firstName, lastName, postcode);
        checkout.finish();
    }

    @Then("I see the products page")
    public void iSeeTheProductsPage() {
        assertThat(inventory.waitUntilLoaded().title()).isEqualTo("Products");
    }

    @Then("the cart badge shows {int}")
    public void theCartBadgeShows(int expected) {
        assertThat(inventory.cartBadgeCount()).as("number on the cart icon").isEqualTo(expected);
    }

    @Then("the cart contains:")
    public void theCartContains(List<String> expectedProducts) {
        assertThat(cart.itemNames()).containsExactlyInAnyOrderElementsOf(expectedProducts);
    }

    @Then("the order is confirmed with {string}")
    public void theOrderIsConfirmedWith(String expectedMessage) {
        assertThat(checkout.confirmation()).isEqualTo(expectedMessage);
    }

    @Then("I see the login error {string}")
    public void iSeeTheLoginError(String expectedError) {
        assertThat(loginPage.errorMessage()).isEqualTo(expectedError);
    }
}
