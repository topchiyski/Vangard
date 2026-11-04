package com.vanguard.mobile.steps;

import com.vanguard.mobile.screens.ProductsScreen;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.openqa.selenium.ScreenOrientation;

/**
 * Ready-made mobile steps (docs/STEP_CATALOGUE.md).
 */
public class MobileCommonSteps {

    private final ProductsScreen products;

    public MobileCommonSteps(ProductsScreen products) {
        this.products = products;
    }

    /**
     * Starts the app (Appium launches it with the session) and waits for the first screen.
     */
    @Given("the app is launched")
    public void theAppIsLaunched() {
        products.waitUntilShown();
    }

    @When("I switch to the web view")
    public void iSwitchToTheWebView() {
        products.switchToWebView();
    }

    @When("I switch back to the app")
    public void iSwitchBackToTheApp() {
        products.switchToNative();
    }

    @When("I rotate the device to {word}")
    public void iRotateTheDevice(String orientation) {
        products.rotate(ScreenOrientation.valueOf(orientation.toUpperCase()));
    }

    @When("I swipe {word}")
    public void iSwipe(String direction) {
        products.swipe(ProductsScreen.Direction.valueOf(direction.toUpperCase()));
    }
}
