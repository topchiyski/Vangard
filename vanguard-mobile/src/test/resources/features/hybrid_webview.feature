# ─────────────────────────────────────────────────────────────────────────────
# EXAMPLE (MOBILE HYBRID) - switch between the native app and a web view inside it.
# Note: Android web views must be debuggable for Appium to enter them; if the switch times out,
#       see docs/MOBILE_SETUP.md#hybrid-apps.
# Run it alone : mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.filter.tags="@hybrid"
# ─────────────────────────────────────────────────────────────────────────────
@mobile @hybrid @example
Feature: Web content inside the app

  @TEST_QA-511
  Scenario: Open a website in the app's web view
    Given the app is launched
    When I open "https://www.saucedemo.com" in the app's web view
    And I switch to the web view
    Then the web page title is "Swag Labs"
    When I switch back to the app
    Then I can go back to the product catalog
