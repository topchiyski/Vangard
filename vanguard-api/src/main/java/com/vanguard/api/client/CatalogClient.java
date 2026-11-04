package com.vanguard.api.client;

import com.vanguard.api.dto.ProductDto;
import com.vanguard.core.context.ScenarioContext;

import java.util.Arrays;
import java.util.List;

/**
 * Reads the product catalog. Used by the E2E module to compare API data with the web shop.
 */
public class CatalogClient extends BaseApiClient {

    public CatalogClient(ScenarioContext context) {
        super(context);
    }

    /**
     * GET /catalog/products, converted to a list of {@link ProductDto}.
     */
    public List<ProductDto> getProducts() {
        ProductDto[] products = get("/catalog/products")
                .then().statusCode(200)
                .extract().as(ProductDto[].class);
        return Arrays.asList(products);
    }
}
