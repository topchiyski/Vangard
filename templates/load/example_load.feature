# TEMPLATE: load feature. NEVER point load tests at systems your team does not own.
# Copy to vanguard-load/src/test/resources/features/<area>_load.feature and replace every TODO.
# Default runs are capped at 5 users / 8 s ("smoke"); -Dload.profile=full uses the table values.
# ─────────────────────────────────────────────────────────────────────────────
# What it tests : TODO
# Uses          : the default simulation (GET requests to the endpoint)
#                 Multi-step journey? Copy ExampleSimulation.java and write: When I run the "<Name>Simulation" load test
# Run it alone  : mvn verify -pl vanguard-load,vanguard-reporting -am -Dcucumber.filter.tags="@todo-area"
# ─────────────────────────────────────────────────────────────────────────────
@load @todo-area
Feature: TODO service performance

  @TEST_QA-000
  Scenario: TODO service handles normal traffic
    Given a load profile "todo-normal" against the "/TODO-items" endpoint:
      | users | rampUpSeconds | durationSeconds | thinkTimeMs |
      | 20    | 20            | 60              | 500         |
    When I run the load test
    Then the error rate is below 1 percent
    And the 95th percentile response time is below 800 ms
