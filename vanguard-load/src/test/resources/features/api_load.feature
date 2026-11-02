# ─────────────────────────────────────────────────────────────────────────────
# REFERENCE EXAMPLE (LOAD) - load tests written in Gherkin, run by Gatling (Java, no Scala).
#
# What it tests : the product API stays fast and error-free under load
# Uses          : ApiBrowseSimulation, CreateUserSimulation
#                 (vanguard-load/src/main/java/com/vanguard/load/simulation)
# Target        : embedded WireMock by default. Set load.base.url ONLY to systems your team owns.
# Profiles      : default "smoke" caps every test at 5 users / 15 s (fast, for PRs).
#                 -Dload.profile=full uses the numbers below.
# Run it alone  : mvn verify -pl vanguard-load,vanguard-reporting -am
#                 mvn verify -pl vanguard-load,vanguard-reporting -am -Dload.profile=full
# Report        : the Allure report links each scenario's Gatling HTML report.
# ─────────────────────────────────────────────────────────────────────────────
@load @example
Feature: Product API performance
  As the operations team
  We want the product API to stay fast under load
  So that customers never wait

  @smoke @TEST_QA-301
  Scenario: Health endpoint answers quickly
    Given a load profile "health-check" against the "health" endpoint:
      | users | rampUpSeconds | durationSeconds |
      | 5     | 2             | 10              |
    When I run the "ApiBrowseSimulation" load test
    Then the error rate is below 1 percent
    And the 95th percentile response time is below 500 ms

  @TEST_QA-302
  Scenario: Product API holds steady under moderate load
    Given a load profile "product-browse" against the "products" endpoint:
      | users | rampUpSeconds | durationSeconds | thinkTimeMs |
      | 50    | 30            | 120             | 200         |
    When I run the "ApiBrowseSimulation" load test
    Then the error rate is below 1 percent
    And the 95th percentile response time is below 500 ms
    And the throughput is at least 5 requests per second

  Scenario Outline: Creating users scales to <users> concurrent users
    Given a load profile "create-users-<users>" against the "users" endpoint:
      | users   | rampUpSeconds | durationSeconds |
      | <users> | 10            | 60              |
    When I run the "CreateUserSimulation" load test
    Then the error rate is below 1 percent
    And the mean response time is below 300 ms

    Examples:
      | users |
      | 10    |
      | 25    |
      | 50    |
