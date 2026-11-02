# ─────────────────────────────────────────────────────────────────────────────
# REFERENCE EXAMPLE (API) - copy this style for new API tests.
#
# What it tests : create, read, update and delete users + JSON schema + errors
# Uses          : UserClient (vanguard-api/src/main/.../client/UserClient.java)
#                 UserSteps  (vanguard-api/src/test/.../steps/UserSteps.java)
#                 user-schema.json (vanguard-api/src/main/resources/schemas)
# Server        : embedded WireMock (stubs in vanguard-core/src/main/resources/wiremock)
# Run it alone  : mvn verify -pl vanguard-api,vanguard-reporting -am -Dcucumber.filter.tags="@users"
# ─────────────────────────────────────────────────────────────────────────────
@api @users @example
Feature: User management API
  As an account administrator
  I want to manage users through the API
  So that customer accounts stay correct

  @smoke @TEST_QA-101
  Scenario: Read an existing user
    When I request the user with id 1
    Then the user is returned with name "Leanne Graham"
    And the user matches the user schema

  @TEST_QA-102
  Scenario: Create a new user
    When I create a user named "Ada Lovelace" with email "ada@example.com"
    Then the user is created with a new id
    And the created user has the email "ada@example.com"

  Scenario: Rename a user
    When I rename the user with id 2 to "Ervin Renamed"
    Then the response status is 200
    And the response field "name" equals "Ervin Renamed"

  Scenario: Delete a user
    When I delete the user with id 3
    Then the user is deleted

  # Data-driven: the scenario runs once per row in Examples.
  Scenario Outline: Read every known user
    When I request the user with id <id>
    Then the user is returned with name "<name>"
    And the user matches the user schema

    Examples:
      | id | name             |
      | 1  | Leanne Graham    |
      | 2  | Ervin Howell     |
      | 3  | Clementine Bauch |

  # Negative tests: check that the API rejects bad requests clearly.
  Scenario: Unknown user is not found
    When I request the user with id 999
    Then the response status is 404
    And the response field "error" equals "User not found"

  Scenario: A user without an email is rejected
    When I try to create a user named "No Email" without an email
    Then the response status is 400
    And the response field "error" equals "email is required"
