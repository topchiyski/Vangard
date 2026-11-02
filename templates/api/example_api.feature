# TEMPLATE: API feature.
# Copy to vanguard-api/src/test/resources/features/<area>.feature and replace every TODO.
# Need a fake endpoint? Add a JSON stub in vanguard-core/src/main/resources/wiremock/mappings
# (see docs/JUNIOR_ONBOARDING_GUIDE.md, Recipe 2).
# ─────────────────────────────────────────────────────────────────────────────
# What it tests : TODO
# Uses          : TODO ExampleClient, ExampleApiSteps
# Run it alone  : mvn verify -pl vanguard-api,vanguard-reporting -am -Dcucumber.filter.tags="@todo-area"
# ─────────────────────────────────────────────────────────────────────────────
@api @todo-area
Feature: TODO short business title

  @TEST_QA-000
  Scenario: TODO create something
    When I create the todo "Write my first test"
    Then the todo is created with the title "Write my first test"

  Scenario: TODO the same check with ready-made steps only
    When I send a GET request to "/todos/1"
    Then the response status is 200
    And the response field "title" is not empty
