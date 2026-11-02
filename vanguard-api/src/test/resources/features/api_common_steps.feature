# ─────────────────────────────────────────────────────────────────────────────
# EXAMPLE: API tests with ZERO Java - only ready-made steps (docs/STEP_CATALOGUE.md).
# Great for quick checks. For bigger suites prefer a client + business steps
# (see user_crud_and_schema.feature).
# Run it alone : mvn verify -pl vanguard-api,vanguard-reporting -am -Dcucumber.filter.tags="@no-code"
# ─────────────────────────────────────────────────────────────────────────────
@api @no-code @example
Feature: API checks written with common steps only

  @smoke
  Scenario: Read a user and validate it
    When I send a GET request to "/users/1"
    Then the response status is 200
    And the response field "username" equals "Bret"
    And the response matches the schema "user-schema.json"
    And the response time is below 2000 ms

  Scenario: Create a user, then update it using the saved id
    When I send a POST request to "/users" with body:
      """
      { "name": "Grace Hopper", "username": "grace", "email": "grace@example.com" }
      """
    Then the response status is 201
    And I save the response field "id" as "userId"
    When I send a PUT request to "/users/{userId}" with body:
      """
      { "name": "Rear Admiral Hopper", "username": "grace", "email": "grace@example.com" }
      """
    Then the response status is 200
    And the response field "id" equals "{userId}"
    And the response field "name" equals "Rear Admiral Hopper"

  Scenario: The catalog lists all products
    When I send a GET request to "/catalog/products"
    Then the response status is 200
    And the response list "$" has 6 items
    And the response field "[0].name" equals "Sauce Labs Backpack"
