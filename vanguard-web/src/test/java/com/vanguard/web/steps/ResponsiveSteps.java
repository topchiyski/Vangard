package com.vanguard.web.steps;

import com.vanguard.web.pages.InventoryPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Steps for phone-sized Chrome (feature: web_responsive.feature).
 */
public class ResponsiveSteps {

    private final InventoryPage inventory;
    private List<String> menuItems = List.of();

    public ResponsiveSteps(InventoryPage inventory) {
        this.inventory = inventory;
    }

    @Then("the screen is {int} pixels wide")
    public void theScreenIsPixelsWide(int expectedWidth) {
        assertThat(inventory.viewportWidth()).as("window.innerWidth").isEqualTo(expectedWidth);
    }

    @When("I look at the menu")
    public void iLookAtTheMenu() {
        menuItems = inventory.menuEntries();
    }

    @Then("the menu offers {string}")
    public void theMenuOffers(String entry) {
        assertThat(menuItems).contains(entry);
    }
}
