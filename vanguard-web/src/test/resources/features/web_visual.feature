# ─────────────────────────────────────────────────────────────────────────────
# EXAMPLE: visual testing with Applitools Eyes.
#
# Without an API key these checks are SKIPPED with a warning (the scenario still passes).
# To run them for real:
#   set APPLITOOLS_API_KEY=<your key>          (Windows)   export APPLITOOLS_API_KEY=...  (Mac/Linux)
#   mvn verify -pl vanguard-web,vanguard-reporting -am -Dvisual.enabled=true -Dcucumber.filter.tags="@visual"
# The first run saves baselines; later runs compare against them.
# ─────────────────────────────────────────────────────────────────────────────
@web @visual @example
Feature: Visual checks of the shop

  Background:
    Given I am logged in as "standard_user"

  @TEST_QA-204
  Scenario: Products page looks exactly as approved
    Then the "Products" page matches its visual baseline

  @TEST_QA-205
  Scenario: Products page looks right even when prices change
    Then the "Products" page matches its visual baseline ignoring prices

  @TEST_QA-206
  Scenario: Products page keeps its layout
    Then the "Products" page layout matches its visual baseline
