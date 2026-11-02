# TEMPLATE: web feature.
# Copy to vanguard-web/src/test/resources/features/<area>.feature and:
# 1. TODO replace the header comment (what / uses / run it alone)
# 2. TODO keep @web; add @desktop or @mobile-emulation; add your Jira key as @TEST_<KEY>
# 3. TODO write steps in business language - no CSS, no ids, no clicks
# ─────────────────────────────────────────────────────────────────────────────
# What it tests : TODO
# Uses          : TODO ExamplePage, ExampleWebSteps
# Run it alone  : mvn verify -pl vanguard-web,vanguard-reporting -am -Dcucumber.filter.tags="@todo-area"
# ─────────────────────────────────────────────────────────────────────────────
@web @desktop @todo-area
Feature: TODO short business title
  As a TODO role
  I want TODO goal
  So that TODO benefit

  @TEST_QA-000
  Scenario: TODO one behaviour per scenario
    Given I am on the TODO page
    When I save the name "Ada"
    Then I see the message "Saved"
