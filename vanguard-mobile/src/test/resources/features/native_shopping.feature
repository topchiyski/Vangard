# ─────────────────────────────────────────────────────────────────────────────
# REFERENCE EXAMPLE (MOBILE NATIVE) - one feature, runs on Android AND iOS.
#
# App           : Sauce Labs "My Demo App" (React Native) - scripts/download-sample-apps
# Uses          : MenuScreen, LoginScreen, ProductsScreen, ProductDetailsScreen, CartScreen
#                 (vanguard-mobile/src/main/java/com/vanguard/mobile/screens)
#                 NativeShoppingSteps (vanguard-mobile/src/test/java/com/vanguard/mobile/steps)
# Platform      : -Dmobile.platform=android (default) or ios, or tag a scenario @android / @ios
# Run it alone  : mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.filter.tags="@native"
# ─────────────────────────────────────────────────────────────────────────────
@mobile @native @example
Feature: Shopping in the mobile app
  As a customer on my phone
  I want to put products in my cart
  So that I can buy them on the go

  @smoke @TEST_QA-501
  Scenario: Add a product to the cart
    Given I am logged in to the app as the standard user
    When I add the product "Sauce Labs Backpack" to the cart in the app
    Then the app cart icon shows 1
    When I open the cart in the app
    Then the app cart contains:
      | Sauce Labs Backpack |

  @TEST_QA-502
  Scenario: Browse the catalog by scrolling
    Given the app is launched
    Then I can scroll to the product "Test.allTheThings() T-Shirt"

  @TEST_QA-503
  Scenario: A locked-out user cannot log in
    Given the app is launched
    When I log in to the app as the locked-out user
    Then the app shows the login error "Sorry, this user has been locked out."
