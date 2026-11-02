package com.vanguard.mobile.steps;

import com.vanguard.mobile.screens.CartScreen;
import com.vanguard.mobile.screens.LoginScreen;
import com.vanguard.mobile.screens.MenuScreen;
import com.vanguard.mobile.screens.ProductDetailsScreen;
import com.vanguard.mobile.screens.ProductsScreen;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * REFERENCE EXAMPLE of mobile step definitions (feature: native_shopping.feature).
 * The SAME steps run on Android and iOS: the Screen Objects pick the right locators.
 */
public class NativeShoppingSteps {

    private final MenuScreen menu;
    private final LoginScreen login;
    private final ProductsScreen products;
    private final ProductDetailsScreen details;
    private final CartScreen cart;

    public NativeShoppingSteps(MenuScreen menu, LoginScreen login, ProductsScreen products,
                               ProductDetailsScreen details, CartScreen cart) {
        this.menu = menu;
        this.login = login;
        this.products = products;
        this.details = details;
        this.cart = cart;
    }

    @Given("I am logged in to the app as the standard user")
    public void iAmLoggedInToTheApp() {
        products.waitUntilShown();
        menu.openLogin();
        login.loginAs(LoginScreen.STANDARD_USER);
        products.waitUntilShown();
    }

    @When("I log in to the app as the locked-out user")
    public void iLogInAsLockedOutUser() {
        menu.openLogin();
        login.loginAs(LoginScreen.LOCKED_OUT_USER);
    }

    @When("I add the product {string} to the cart in the app")
    public void iAddTheProductToTheCartInTheApp(String productName) {
        products.openProduct(productName);
        details.addToCart();
    }

    @When("I open the cart in the app")
    public void iOpenTheCartInTheApp() {
        products.openCart();
    }

    @Then("the app cart icon shows {int}")
    public void theAppCartIconShows(int expected) {
        assertThat(products.cartCount()).as("number on the cart icon").isEqualTo(expected);
    }

    @Then("the app cart contains:")
    public void theAppCartContains(List<String> expected) {
        assertThat(cart.itemNames()).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Then("I can scroll to the product {string}")
    public void iCanScrollToTheProduct(String productName) {
        assertThat(products.scrollToProduct(productName)).as("%s visible after scrolling", productName).isTrue();
    }

    @Then("the app shows the login error {string}")
    public void theAppShowsTheLoginError(String expected) {
        assertThat(login.errorMessage()).isEqualTo(expected);
    }
}
