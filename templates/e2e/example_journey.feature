# TEMPLATE: cross-layer journey.
# Copy to vanguard-e2e/src/test/resources/features/<journey>.feature and replace every TODO.
# Add @web when the journey uses the browser (that tag starts Chrome).
# ─────────────────────────────────────────────────────────────────────────────
# What it tests : TODO
# Uses          : TODO UserClient (api) + LoginPage (web) + ExampleJourneySteps
# Run it alone  : mvn verify -pl vanguard-e2e,vanguard-reporting -am -Dcucumber.filter.tags="@todo-journey"
# ─────────────────────────────────────────────────────────────────────────────
@e2e @web @todo-journey
Feature: TODO journey title

  @TEST_QA-000
  Scenario: TODO data created by the API appears in the web app
    Given a TODO user created through the API
    When the TODO user opens the web app
    Then the TODO web app shows the user's name
