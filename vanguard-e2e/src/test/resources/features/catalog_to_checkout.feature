# ─────────────────────────────────────────────────────────────────────────────
# REFERENCE EXAMPLE (E2E) - one journey across layers: API data → web shop → cross-check.
#
# What it tests : the Catalog API and the web shop agree on products and prices
# Uses          : CatalogClient (vanguard-api) + LoginPage/InventoryPage/CartPage/CheckoutPage (vanguard-web)
#                 CatalogJourneySteps (vanguard-e2e/src/test/java/com/vanguard/e2e/steps)
# Data flow     : ONLY through ScenarioContext (CATALOG → SELECTED_PRODUCT → ORDER_TOTAL)
# Run it alone  : mvn verify -pl vanguard-e2e,vanguard-reporting -am
# ─────────────────────────────────────────────────────────────────────────────
@e2e @web @example
Feature: Catalog API and web shop agree
  As the product owner
  I want the shop to sell products exactly as the catalog defines them
  So that customers always pay the right price

  Background:
    Given the product catalog from the Catalog API
    And I am logged in to the web shop as "standard_user"

  @smoke @TEST_QA-401
  Scenario: Buy the cheapest catalog product at the catalog price
    When I buy the cheapest catalog product
    Then the checkout item total equals the catalog price
    And the order is confirmed

  @TEST_QA-402
  Scenario: Every catalog product is sold at its catalog price
    Then every catalog product is sold in the web shop at its catalog price

  Scenario: Mix ready-made API and web steps in one journey
    When I send a GET request to "/catalog/products"
    Then the response status is 200
    And I save the response field "[0].name" as "firstProduct"
    And the page URL contains "inventory.html"
