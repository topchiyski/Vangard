# Step Catalogue

Ready-made steps you can use in feature files **without writing Java**.
Text in `"quotes"` can contain `{name}` placeholders saved earlier (see [Context](#context-all-modules)).

> In IntelliJ or VS Code with the Cucumber plugin, type the first words of a step and press `Ctrl+Space` to see suggestions.

## API (vanguard-api, vanguard-e2e)

| Step | Example |
|---|---|
| `When I send a <METHOD> request to "<path>"` | `When I send a GET request to "/users/1"` |
| `When I send a <METHOD> request to "<path>" with body:` + a docstring | see below |
| `Then the response status is <code>` | `Then the response status is 201` |
| `Then the response field "<path>" equals "<value>"` | `Then the response field "username" equals "Bret"` |
| `Then the response field "<path>" is not empty` | `Then the response field "id" is not empty` |
| `Then the response list "<path>" has <n> items` | `Then the response list "$" has 6 items` |
| `Then the response matches the schema "<file>"` | `Then the response matches the schema "user-schema.json"` |
| `Then the response time is below <ms> ms` | `Then the response time is below 2000 ms` |
| `Then I save the response field "<path>" as "<name>"` | `Then I save the response field "id" as "userId"` |

`<METHOD>` is `GET`, `POST`, `PUT`, `PATCH` or `DELETE`. Schemas live in `vanguard-api/src/main/resources/schemas/`.

**Field paths:** `name` is a top-level field, `address.city` is a nested field, `[0].name` is the first item of a list, and `$` is the whole body.

```gherkin
When I send a POST request to "/users" with body:
  """
  { "name": "Grace Hopper", "username": "grace", "email": "grace@example.com" }
  """
Then the response status is 201
And I save the response field "id" as "userId"
When I send a GET request to "/users/{userId}"
```

## Web (vanguard-web, vanguard-e2e)

| Step | Example |
|---|---|
| `Given I open the "<path>" page` | `Given I open the "/inventory.html" page` |
| `Then the page URL contains "<text>"` | `Then the page URL contains "checkout"` |
| `Then the page title is "<title>"` | `Then the page title is "Swag Labs"` |

Business actions (logging in, adding to the cart) belong in your own steps backed by Page Objects. See [Recipe 1](JUNIOR_ONBOARDING_GUIDE.md#recipe-1-a-new-web-test).
The shop examples also offer `Given I am logged in as "<user>"` and `When I add the "<product>" to the cart` in `ShoppingSteps`.

**Visual** (scenarios tagged `@visual`; skipped with a warning when visual testing is off):

| Step |
|---|
| `Then the "<name>" page matches its visual baseline` |
| `Then the "<name>" page matches its visual baseline ignoring prices` |
| `Then the "<name>" page layout matches its visual baseline` |

## Mobile (vanguard-mobile)

| Step | Example |
|---|---|
| `Given the app is launched` | waits for the first screen |
| `When I switch to the web view` | for `@hybrid` apps |
| `When I switch back to the app` | |
| `When I rotate the device to <landscape\|portrait>` | `When I rotate the device to landscape` |
| `When I swipe <up\|down\|left\|right>` | `When I swipe up` |
| `Then the "<name>" screen matches its visual baseline` | `@visual` scenarios |

## Load (vanguard-load)

| Step | Example |
|---|---|
| `Given a load profile "<name>" against the "<endpoint>" endpoint:` + table | see below |
| `When I run the "<Simulation>" load test` | `When I run the "ApiBrowseSimulation" load test` |
| `Then the error rate is below <n> percent` | `Then the error rate is below 1 percent` |
| `Then the <50\|95\|99>th percentile response time is below <ms> ms` | `Then the 95th percentile response time is below 500 ms` |
| `Then the mean response time is below <ms> ms` | |
| `Then the throughput is at least <n> requests per second` | |
| `Then at least <n> requests were sent` | |

```gherkin
Given a load profile "browse" against the "products" endpoint:
  | users | rampUpSeconds | durationSeconds | thinkTimeMs |
  | 50    | 30            | 120             | 200         |
```

Table columns are optional. The defaults are 10 users, 10 s ramp-up, 30 s duration and 200 ms think time.
Endpoints are `health`, `products`, `users`, or any path that starts with `/`.

## Context (all modules)

| Step | Example |
|---|---|
| `Given I remember "<value>" as "<name>"` | `Given I remember "Sauce Labs Backpack" as "product"` |

Later steps can use `{product}`. In your own Java steps, call `context.resolve(text)` to replace placeholders.
