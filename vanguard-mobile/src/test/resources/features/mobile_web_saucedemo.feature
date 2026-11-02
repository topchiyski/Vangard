# ─────────────────────────────────────────────────────────────────────────────
# EXAMPLE (MOBILE WEB) - the shop website in the phone's real browser
# (Chrome on Android, Safari on iOS). Reuses the WEB module's Page Objects unchanged.
# Run it alone : mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.filter.tags="@mobileweb"
# ─────────────────────────────────────────────────────────────────────────────
@mobile @mobileweb @example
Feature: Shop website on a real phone browser

  @smoke @TEST_QA-521
  Scenario: Add a product to the cart on the phone browser
    Given I am logged in to the shop website as "standard_user"
    When I add the "Sauce Labs Backpack" to the cart on the website
    Then the website cart badge shows 1
