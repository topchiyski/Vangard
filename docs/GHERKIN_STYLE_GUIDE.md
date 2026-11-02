# Gherkin Style Guide

Good feature files can be read by anyone on the team, including people who never read code.

## 1. Describe behaviour, not clicks (declarative, not imperative)

```gherkin
# ❌ Imperative: every UI detail, breaks often, hard to read
When I type "standard_user" into "#user-name"
And I type "secret_sauce" into "#password"
And I click "#login-button"

# ✅ Declarative: what the user wants; the details live in Java
Given I am logged in as "standard_user"
```

## 2. One behaviour per scenario

A scenario answers **one** question. If you are tempted to write "and then also check…", write a second scenario.
Keep scenarios to about 3-8 steps.

## 3. Given / When / Then each have a job

| Keyword | Job | Example |
|---|---|---|
| `Given` | The starting situation | `Given I am logged in as "standard_user"` |
| `When` | The action being tested (ideally one) | `When I add the "Sauce Labs Backpack" to the cart` |
| `Then` | The expected outcome, as a check | `Then the cart badge shows 1` |
| `And` / `But` | Continues the previous keyword | |

## 4. Background: shared starting steps

Use `Background` when **every** scenario in the file starts the same way. Keep it short.

```gherkin
Background:
  Given I am logged in as "standard_user"
```

## 5. Scenario Outline: the same test with different data

```gherkin
Scenario Outline: Read every known user
  When I request the user with id <id>
  Then the user is returned with name "<name>"

  Examples:
    | id | name          |
    | 1  | Leanne Graham |
    | 2  | Ervin Howell  |
```

## 6. Data tables: lists or records inside a step

```gherkin
Then the cart contains:
  | Sauce Labs Backpack   |
  | Sauce Labs Bike Light |
```

## 7. Names and tags

- **Feature:** a business capability ("Online shop purchase"), plus the optional `As a / I want / So that`.
- **Scenario:** the behaviour, in plain words ("A locked-out user cannot log in").
- **File name:** `snake_case.feature`, named after the feature.
- **Every feature** starts with the header comment: *What it tests / Uses / Run it alone*.
- **Tags** (full list in the [Cheat Sheet](CHEATSHEET.md#tags)): one type tag (`@api`, `@web`, `@mobile`, `@load`, `@e2e`), optionally `@smoke`, and `@TEST_<KEY>` for Jira.

## 8. Reuse existing steps

Before writing a new step, search for an existing one: in the [Step Catalogue](STEP_CATALOGUE.md), or in IntelliJ / VS Code with `Ctrl+Space`.
Two steps that mean the same thing ("I log in as" and "I sign in as") confuse everyone.
