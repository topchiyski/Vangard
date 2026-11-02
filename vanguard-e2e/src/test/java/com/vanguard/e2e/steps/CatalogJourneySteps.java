package com.vanguard.e2e.steps;

import com.vanguard.api.client.CatalogClient;
import com.vanguard.api.dto.ProductDto;
import com.vanguard.core.context.ContextKey;
import com.vanguard.core.context.ScenarioContext;
import com.vanguard.web.pages.CartPage;
import com.vanguard.web.pages.CheckoutPage;
import com.vanguard.web.pages.InventoryPage;
import com.vanguard.web.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * REFERENCE EXAMPLE of a cross-layer journey (feature: catalog_to_checkout.feature).
 *
 * <p>It REUSES {@link CatalogClient} from vanguard-api and the Page Objects from vanguard-web: nothing is copied.
 * Data moves between the API layer and the web layer ONLY through {@link ScenarioContext}.
 */
public class CatalogJourneySteps {

    private final CatalogClient catalogApi;
    private final LoginPage loginPage;
    private final InventoryPage inventory;
    private final CartPage cart;
    private final CheckoutPage checkout;
    private final ScenarioContext context;

    public CatalogJourneySteps(CatalogClient catalogApi, LoginPage loginPage, InventoryPage inventory,
                               CartPage cart, CheckoutPage checkout, ScenarioContext context) {
        this.catalogApi = catalogApi;
        this.loginPage = loginPage;
        this.inventory = inventory;
        this.cart = cart;
        this.checkout = checkout;
        this.context = context;
    }

    // ---------- API layer ----------

    @Given("the product catalog from the Catalog API")
    public void theProductCatalogFromTheApi() {
        List<ProductDto> products = catalogApi.getProducts();
        assertThat(products).as("catalog from the API").isNotEmpty();
        context.set(ContextKey.CATALOG, products);
    }

    // ---------- Web layer ----------

    @Given("I am logged in to the web shop as {string}")
    public void iAmLoggedInToTheWebShop(String username) {
        loginPage.open().loginAs(username);
        inventory.waitUntilLoaded();
    }

    @When("I buy the cheapest catalog product")
    public void iBuyTheCheapestCatalogProduct() {
        ProductDto cheapest = catalog().stream().min(Comparator.comparing(ProductDto::price)).orElseThrow();
        context.set(ContextKey.SELECTED_PRODUCT, cheapest);

        inventory.addToCart(cheapest.name());
        inventory.openCart();
        cart.checkout();
        checkout.enterCustomer("E2E", "Tester", "10115");
        context.set(ContextKey.ORDER_TOTAL, checkout.itemTotal());
        checkout.finish();
    }

    // ---------- Cross-layer checks ----------

    @Then("the checkout item total equals the catalog price")
    public void theCheckoutTotalEqualsTheCatalogPrice() {
        BigDecimal catalogPrice = context.get(ContextKey.SELECTED_PRODUCT, ProductDto.class).price();
        assertThat(context.get(ContextKey.ORDER_TOTAL, BigDecimal.class)).isEqualByComparingTo(catalogPrice);
    }

    @Then("the order is confirmed")
    public void theOrderIsConfirmed() {
        assertThat(checkout.confirmation()).isEqualTo("Thank you for your order!");
    }

    @Then("every catalog product is sold in the web shop at its catalog price")
    public void everyCatalogProductIsSoldAtItsCatalogPrice() {
        assertThat(inventory.productNames())
                .containsExactlyInAnyOrderElementsOf(catalog().stream().map(ProductDto::name).toList());
        for (ProductDto product : catalog()) {
            assertThat(inventory.priceOf(product.name()))
                    .as("web price of %s", product.name())
                    .isEqualByComparingTo(product.price());
        }
    }

    @SuppressWarnings("unchecked")
    private List<ProductDto> catalog() {
        return context.get(ContextKey.CATALOG, List.class);
    }
}
