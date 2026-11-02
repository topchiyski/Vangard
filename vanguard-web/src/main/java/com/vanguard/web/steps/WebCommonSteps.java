package com.vanguard.web.steps;

import com.vanguard.core.context.ScenarioContext;
import com.vanguard.web.pages.CurrentPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ready-made web steps for navigation and simple page checks (docs/STEP_CATALOGUE.md).
 * Business actions (log in, add to cart...) belong in Page Objects + your own steps.
 */
public class WebCommonSteps {

    private final CurrentPage page;
    private final ScenarioContext context;

    public WebCommonSteps(CurrentPage page, ScenarioContext context) {
        this.page = page;
        this.context = context;
    }

    @Given("I open the {string} page")
    public void iOpenThePage(String path) {
        page.openPath(context.resolve(path));
    }

    @Then("the page URL contains {string}")
    public void thePageUrlContains(String fraction) {
        page.waitForUrl(context.resolve(fraction));
    }

    @Then("the page title is {string}")
    public void thePageTitleIs(String expected) {
        assertThat(page.pageTitle()).as("Browser tab title").isEqualTo(context.resolve(expected));
    }
}
