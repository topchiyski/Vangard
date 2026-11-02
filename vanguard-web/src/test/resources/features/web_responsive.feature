# ─────────────────────────────────────────────────────────────────────────────
# EXAMPLE: phone-sized Chrome (mobile emulation) - fast responsive checks, no device needed.
# Real Android/iOS browsers live in vanguard-mobile (@mobileweb).
#
# The @mobile-emulation tag makes Chrome pretend to be a Pixel 7 (412 px wide).
# Run it alone : mvn verify -pl vanguard-web,vanguard-reporting -am -Dcucumber.filter.tags="@responsive"
# ─────────────────────────────────────────────────────────────────────────────
@web @mobile-emulation @responsive @example
Feature: Shop works on a phone-sized screen

  Background:
    Given I am logged in as "standard_user"

  @smoke @TEST_QA-203
  Scenario: The shop uses the phone viewport
    Then the screen is 412 pixels wide
    And I see the products page

  Scenario: Navigate with the burger menu and buy on a phone
    When I look at the menu
    Then the menu offers "All Items"
    And the menu offers "Logout"
    When I add the "Sauce Labs Onesie" to the cart
    Then the cart badge shows 1
    When I open the cart
    Then the cart contains:
      | Sauce Labs Onesie |
