package com.vanguard.mobile.steps;

import com.vanguard.mobile.screens.MenuScreen;
import com.vanguard.mobile.screens.ProductsScreen;
import com.vanguard.mobile.screens.WebViewScreen;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

/** Native ↔ web view switching (feature: hybrid_webview.feature). */
public class HybridSteps {

    private final MenuScreen menu;
    private final WebViewScreen webView;
    private final ProductsScreen products;

    public HybridSteps(MenuScreen menu, WebViewScreen webView, ProductsScreen products) {
        this.menu = menu;
        this.webView = webView;
        this.products = products;
    }

    @When("I open {string} in the app's web view")
    public void iOpenInTheWebView(String url) {
        menu.openWebView();
        webView.openSite(url);
    }

    @Then("the web page title is {string}")
    public void theWebPageTitleIs(String expected) {
        assertThat(webView.webPageTitle()).isEqualTo(expected);
    }

    @Then("I can go back to the product catalog")
    public void iCanGoBackToTheCatalog() {
        menu.openCatalog();
        products.waitUntilShown();
    }
}
