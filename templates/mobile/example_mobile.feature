# TEMPLATE: mobile feature (runs on Android AND iOS).
# Copy to vanguard-mobile/src/test/resources/features/<area>.feature and replace every TODO.
# Tags: @mobile + ONE of @native / @hybrid / @mobileweb. Add @android or @ios only if the
# scenario must run on one platform; otherwise -Dmobile.platform chooses.
# ─────────────────────────────────────────────────────────────────────────────
# What it tests : TODO
# Uses          : TODO ExampleScreen, ExampleMobileSteps
# Run it alone  : mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.filter.tags="@todo-area"
# ─────────────────────────────────────────────────────────────────────────────
@mobile @native @todo-area
Feature: TODO short business title

  @TEST_QA-000
  Scenario: TODO one behaviour
    Given the app is launched
    When I save my TODO settings
    Then the TODO screen title is "Settings"
