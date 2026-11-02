# ─────────────────────────────────────────────────────────────────────────────
# REFERENCE EXAMPLE (WEB) - copy this style for new web tests.
#
# What it tests : login, add to cart, checkout, locked-out user
# Uses          : LoginPage, InventoryPage, CartPage, CheckoutPage
#                 (vanguard-web/src/main/java/com/vanguard/web/pages)
#                 ShoppingSteps (vanguard-web/src/test/java/com/vanguard/web/steps)
# Browser       : desktop Chrome 1920x1080 (headless; add -Dbrowser.headless=false to watch it)
# Run it alone  : mvn verify -pl vanguard-web,vanguard-reporting -am -Dcucumber.filter.tags="@shopping"
# ─────────────────────────────────────────────────────────────────────────────
@web @desktop @shopping @example
Feature: Online shop purchase
  As a customer
  I want to buy products online
  So that they are delivered to me

  @smoke @TEST_QA-201
  Scenario: Buy two products
    Given I am logged in as "standard_user"
    When I add the "Sauce Labs Backpack" to the cart
    And I add the "Sauce Labs Bike Light" to the cart
    Then the cart badge shows 2
    When I open the cart
    Then the cart contains:
      | Sauce Labs Backpack   |
      | Sauce Labs Bike Light |
    When I check out as "Ada" "Lovelace" with postcode "10115"
    Then the order is confirmed with "Thank you for your order!"

  @TEST_QA-202
  Scenario: A locked-out user cannot log in
    Given I am on the login page
    When I log in as "locked_out_user"
    Then I see the login error "Epic sadface: Sorry, this user has been locked out."

  Scenario: Navigate with ready-made steps
    Given I am logged in as "standard_user"
    Then the page URL contains "inventory.html"
    And the page title is "Swag Labs"
