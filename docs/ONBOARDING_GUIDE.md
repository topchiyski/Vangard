# Onboarding Guide

This guide takes you from "never seen this project" to "I added my own tests", one step at a time.
Every recipe uses **complete code**: copy it as shown and it works.
Unfamiliar words are explained in the [Glossary](GLOSSARY.md).

**Contents**
- [Day 1: run the tests and read the report](#day-1-run-the-tests-and-read-the-report)
- [How a Vanguard test is built](#how-a-vanguard-test-is-built)
- [Recipe 1: a new web test](#recipe-1-a-new-web-test)
- [Recipe 2: a new API test](#recipe-2-a-new-api-test)
- [Recipe 3: a new mobile test (Android + iOS)](#recipe-3-a-new-mobile-test-android--ios)
- [Recipe 4: a new load test](#recipe-4-a-new-load-test)
- [Recipe 5: share data between steps](#recipe-5-share-data-between-steps)
- [Recipe 6: visual checks and Jira Xray links](#recipe-6-visual-checks-and-jira-xray-links)
- [Golden Rules: what NEVER to do](#golden-rules-what-never-to-do)

---

## Day 1: run the tests and read the report

**1. Install the tools.** You need JDK 21, Maven, Google Chrome and Git. Check them:

```bash
java -version
mvn -version
```

What you should see: `java version "21..."`, and Maven showing `Java version: 21...`.

**2. Run the smoke tests.** Smoke tests are the most important checks, one or two per area:

```bash
mvn clean verify -Dcucumber.filter.tags="@smoke"
```

What you should see at the end (the numbers may differ):

```
==================== VANGUARD RESULTS ====================
Module                   Passed   Failed  Skipped
vanguard-api                  2        0        0
vanguard-e2e                  1        0        0
vanguard-load                 1        0        0
vanguard-web                  2        0        0
==========================================================
[INFO] BUILD SUCCESS
```

**3. Open the report:**

```bash
mvn -N allure:serve
```

A browser tab opens. Click **Suites** on the left and open any scenario. You will see:
- each step, with how long it took;
- for API tests, the full request and response (click **Request** / **Response**);
- for failed web tests, a **screenshot**, the page URL and the browser console log.

**4. See a failure on purpose.** Open `vanguard-api/src/test/resources/features/user_crud_and_schema.feature`.
In the first scenario, change `"Leanne Graham"` to `"Someone Else"`. Then run:

```bash
mvn verify -pl vanguard-api,vanguard-reporting -am -Dcucumber.filter.tags="@TEST_QA-101"
```

What you should see: the scenario runs twice (Vanguard retries a failed scenario once), then:

```
  FAILED: vanguard-api :: Read an existing user (classpath:features/user_crud_and_schema.feature)
java.lang.IllegalStateException: 1 scenario(s) failed. Open the Allure report: mvn -N allure:serve
[ERROR] BUILD FAILURE
```

Open the report again and find the red scenario: it shows the expected value and the actual value.
**Undo your change** (back to `"Leanne Graham"`).

✅ **Check yourself**
- [ ] I can run all tests, one module, and one tag.
- [ ] I can open the Allure report and find a failed step.

---

## How a Vanguard test is built

Every test, whatever its type, has the same three layers:

```
Feature file (Gherkin)         → WHAT is tested, in business language. Anyone can read it.
   ↓ each line is matched by
Step definition (Java)         → 1-3 lines that call the layer below, then check the result.
   ↓ calls
Page Object / Screen Object /  → HOW to talk to the system: locators, HTTP calls, waits.
API client / Simulation
```

You never create objects with `new`: Cucumber hands them to you through the constructor.
This is called *dependency injection* (see the [Glossary](GLOSSARY.md)).

Before you write Java, check the [Step Catalogue](STEP_CATALOGUE.md). A ready-made step may already do what you need.

---

## Recipe 1: a new web test

**Goal:** check that a product's detail page shows the right price.
**Time:** 15 minutes. **You will create:** a Page Object, a step class and a feature file.

### Step 1: write the feature

Create `vanguard-web/src/test/resources/features/product_details.feature`:

```gherkin
# What it tests : the product detail page shows the right name and price
# Uses          : ProductDetailsPage, ProductDetailsSteps
# Run it alone  : mvn verify -pl vanguard-web,vanguard-reporting -am -Dcucumber.filter.tags="@product-details"
@web @desktop @product-details
Feature: Product details
  As a customer
  I want to see a product's details
  So that I know what I am buying

  @TEST_QA-210
  Scenario: Product details show the right price
    Given I am logged in as "standard_user"
    When I open the details of "Sauce Labs Fleece Jacket"
    Then the details page shows "49.99" for "Sauce Labs Fleece Jacket"
```

The `Given` step already exists in `ShoppingSteps`, so you get it for free. The other two are new.

Run it now, before writing any Java:

```bash
mvn verify -pl vanguard-web,vanguard-reporting -am -Dcucumber.filter.tags="@product-details"
```

What you should see: the scenario fails as **undefined**, and Cucumber prints the Java method you need:

```
The step 'I open the details of "Sauce Labs Fleece Jacket"' is undefined.
You can implement this step using the snippet(s) below:

@When("I open the details of {string}")
public void iOpenTheDetailsOf(String string) {
```

### Step 2: create the Page Object

Find the locators. Open https://www.saucedemo.com in Chrome, log in, click a product, then right-click the price and choose **Inspect**.
You will see `data-test="inventory-item-price"`. Prefer `id` and `data-test` attributes: they rarely change.

Create `vanguard-web/src/main/java/com/vanguard/web/pages/ProductDetailsPage.java`:

```java
package com.vanguard.web.pages;

import org.openqa.selenium.By;

import java.math.BigDecimal;

/** The page of one product (/inventory-item.html). */
public class ProductDetailsPage extends BasePage {

    private static final By NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By BACK = By.id("back-to-products");

    /** Opens a product by clicking its name on the products page. */
    public void openFromProductList(String productName) {
        click(By.xpath("//*[@data-test='inventory-item-name' and normalize-space()='" + productName + "']"));
        waitForUrlContains("inventory-item.html");
    }

    public String name() {
        return getText(NAME);
    }

    /** The price shown, e.g. 49.99. */
    public BigDecimal price() {
        return new BigDecimal(getText(PRICE).replace("$", ""));
    }

    public void backToProducts() {
        click(BACK);
    }
}
```

Notice:
- locators are `private static final`, so only this class knows them;
- `click`, `getText` and `waitForUrlContains` come from `BasePage` and **wait automatically**;
- methods return plain data (`String`, `BigDecimal`), never Selenium elements.

### Step 3: create the step definitions

Create `vanguard-web/src/test/java/com/vanguard/web/steps/ProductDetailsSteps.java`:

```java
package com.vanguard.web.steps;

import com.vanguard.web.pages.ProductDetailsPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDetailsSteps {

    private final ProductDetailsPage details;

    // Cucumber creates the page for us - never write "new ProductDetailsPage()".
    public ProductDetailsSteps(ProductDetailsPage details) {
        this.details = details;
    }

    @When("I open the details of {string}")
    public void iOpenTheDetailsOf(String productName) {
        details.openFromProductList(productName);
    }

    @Then("the details page shows {string} for {string}")
    public void theDetailsPageShows(String expectedPrice, String productName) {
        assertThat(details.name()).isEqualTo(productName);
        assertThat(details.price()).isEqualByComparingTo(expectedPrice);
    }
}
```

### Step 4: tag it and run it

`@TEST_QA-210` links the scenario to the Jira test `QA-210` (see [Recipe 6](#recipe-6-visual-checks-and-jira-xray-links)). Run:

```bash
mvn verify -pl vanguard-web,vanguard-reporting -am -Dcucumber.filter.tags="@product-details"
```

What you should see:

```
vanguard-web                  1        0        0
[INFO] BUILD SUCCESS
```

Want to watch it? Add `-Dbrowser.headless=false`. For a phone-sized browser, add `-Dbrowser.mode=mobile`.

✅ **Check yourself**
- [ ] My feature file has no CSS, ids or clicks in it.
- [ ] My step methods are 1-3 lines and end with an `assertThat`.
- [ ] My Page Object has private locators and uses `click` / `type` / `getText`.

---

## Recipe 2: a new API test

### Part A: no Java at all (ready-made steps)

Create `vanguard-api/src/test/resources/features/user_two.feature`:

```gherkin
@api @user-two
Feature: Second user

  Scenario: The second user is Ervin
    When I send a GET request to "/users/2"
    Then the response status is 200
    And the response field "name" equals "Ervin Howell"
    And the response matches the schema "user-schema.json"
```

Run it:

```bash
mvn verify -pl vanguard-api,vanguard-reporting -am -Dcucumber.filter.tags="@user-two"
```

What you should see: `vanguard-api  1  0  0` and `BUILD SUCCESS`. All ready-made steps are listed in the [Step Catalogue](STEP_CATALOGUE.md).

### Part B: a new endpoint with a client, a record DTO and a schema

**Goal:** test a new `GET /posts/{id}` endpoint. Our sample API is a local fake server (WireMock), so first we teach it the new endpoint.

**Step 1: add a WireMock stub** (a fake answer). Create `vanguard-core/src/main/resources/wiremock/mappings/posts-get.json`:

```json
{
  "name": "GET a blog post",
  "request": { "method": "GET", "urlPath": "/posts/1" },
  "response": {
    "status": 200,
    "headers": { "Content-Type": "application/json" },
    "jsonBody": { "id": 1, "userId": 1, "title": "Hello Vanguard", "body": "My first stub" }
  }
}
```

> With a real API you skip this step. Instead you set `-Dapi.base.url=https://your-api` (or put it in `config/<env>.properties`).

**Step 2: describe the JSON as a record.** Create `vanguard-api/src/main/java/com/vanguard/api/dto/PostDto.java`:

```java
package com.vanguard.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/** A blog post. Field names match the JSON property names. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record PostDto(Integer id, Integer userId, String title, String body) {
}
```

A `record` is a small immutable class. Java writes the constructor, getters (`post.title()`), `equals` and `toString` for you.

**Step 3: add a JSON schema.** A schema describes the *shape* of the JSON (which fields exist, and their types).
Create `vanguard-api/src/main/resources/schemas/post-schema.json`:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Post",
  "type": "object",
  "required": ["id", "userId", "title", "body"],
  "properties": {
    "id": { "type": "integer" },
    "userId": { "type": "integer" },
    "title": { "type": "string", "minLength": 1 },
    "body": { "type": "string" }
  }
}
```

**Step 4: create the client.** Create `vanguard-api/src/main/java/com/vanguard/api/client/PostClient.java`:

```java
package com.vanguard.api.client;

import com.vanguard.core.context.ScenarioContext;
import io.restassured.response.Response;

/** Blog post endpoints. */
public class PostClient extends BaseApiClient {

    public PostClient(ScenarioContext context) {
        super(context);
    }

    /** GET /posts/{id} */
    public Response getPost(int id) {
        return get("/posts/{id}", id);
    }
}
```

`BaseApiClient` adds the base URL, JSON headers and Allure logging. It also saves every response in `ScenarioContext`.

**Step 5: create the steps.** Create `vanguard-api/src/test/java/com/vanguard/api/steps/PostSteps.java`:

```java
package com.vanguard.api.steps;

import com.vanguard.api.client.PostClient;
import com.vanguard.api.dto.PostDto;
import com.vanguard.core.context.ContextKey;
import com.vanguard.core.context.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class PostSteps {

    private final PostClient posts;
    private final ScenarioContext context;

    public PostSteps(PostClient posts, ScenarioContext context) {
        this.posts = posts;
        this.context = context;
    }

    @When("I read the blog post with id {int}")
    public void iReadTheBlogPost(int id) {
        posts.getPost(id);
    }

    @Then("the blog post has the title {string}")
    public void theBlogPostHasTheTitle(String expectedTitle) {
        PostDto post = context.get(ContextKey.LAST_RESPONSE, Response.class)
                .then().statusCode(200)
                .extract().as(PostDto.class);
        assertThat(post.title()).isEqualTo(expectedTitle);
    }
}
```

**Step 6: write the feature.** Create `vanguard-api/src/test/resources/features/blog_posts.feature`:

```gherkin
# Run it alone : mvn verify -pl vanguard-api,vanguard-reporting -am -Dcucumber.filter.tags="@posts"
@api @posts
Feature: Blog posts API

  @TEST_QA-110
  Scenario: Read a blog post
    When I read the blog post with id 1
    Then the blog post has the title "Hello Vanguard"
    And the response matches the schema "post-schema.json"
```

The last line is a ready-made step: business steps and common steps mix freely. Run it:

```bash
mvn verify -pl vanguard-api,vanguard-reporting -am -Dcucumber.filter.tags="@posts"
```

What you should see: `vanguard-api  1  0  0`, `BUILD SUCCESS`. In the Allure report, the step shows the full request and response.

✅ **Check yourself**
- [ ] The record's field names match the JSON.
- [ ] My client has one small method per endpoint.
- [ ] I used `BigDecimal` for any money values.

---

## Recipe 3: a new mobile test (Android + iOS)

**First do the [Mobile Setup](MOBILE_SETUP.md)** (device or emulator, Appium, sample apps).

**Goal:** open the app's *About* screen from the menu. **One Screen Object works on Android AND iOS.**

### Step 1: find the locators with Appium Inspector

Start the app in Appium Inspector (see [MOBILE_SETUP.md](MOBILE_SETUP.md#appium-inspector)). Open the menu, tap **About**, then click the screen.
The **accessibility id** is `about screen` on both platforms. The menu entry is `menu item about`.

> **Why accessibility ids?** They are the same on Android and iOS when the app developers set them, and they don't change when the layout changes.
> If the two platforms differ, use `new MobileLocator(androidBy, iosBy)`. `MenuScreen` has an example.

### Step 2: add a menu action and a Screen Object

In `vanguard-mobile/src/main/java/com/vanguard/mobile/screens/MenuScreen.java`, add a locator and a method next to the existing ones:

```java
    private static final MobileLocator ABOUT = MobileLocator.accessibilityId("menu item about");

    public void openAbout() {
        tap(OPEN_MENU);
        tap(ABOUT);
    }
```

Create `vanguard-mobile/src/main/java/com/vanguard/mobile/screens/AboutScreen.java`:

```java
package com.vanguard.mobile.screens;

/** The "About" screen. Same accessibility id on Android and iOS. */
public class AboutScreen extends BaseScreen {

    private static final MobileLocator SCREEN = MobileLocator.accessibilityId("about screen");

    /** Waits for the screen; fails with a clear message if it never appears. */
    public void waitUntilShown() {
        waitUntilVisible(SCREEN);
    }
}
```

### Step 3: steps and feature

Create `vanguard-mobile/src/test/java/com/vanguard/mobile/steps/AboutSteps.java`:

```java
package com.vanguard.mobile.steps;

import com.vanguard.mobile.screens.AboutScreen;
import com.vanguard.mobile.screens.MenuScreen;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AboutSteps {

    private final MenuScreen menu;
    private final AboutScreen about;

    public AboutSteps(MenuScreen menu, AboutScreen about) {
        this.menu = menu;
        this.about = about;
    }

    @When("I open the About screen")
    public void iOpenTheAboutScreen() {
        menu.openAbout();
    }

    @Then("I see the About screen")
    public void iSeeTheAboutScreen() {
        about.waitUntilShown();
    }
}
```

Create `vanguard-mobile/src/test/resources/features/about.feature`:

```gherkin
@mobile @native @about
Feature: About the app

  @TEST_QA-540
  Scenario: Open the About screen from the menu
    Given the app is launched
    When I open the About screen
    Then I see the About screen
```

### Step 4: run it

```bash
# Check that every step has code, without starting a device (fast):
mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.filter.tags="@about" -Dcucumber.execution.dry-run=true

# Android emulator:
mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.filter.tags="@about"

# iOS simulator (Mac only):
mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.filter.tags="@about" -Dmobile.platform=ios

# BrowserStack (no local device needed):
mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.filter.tags="@about" -Dmobile.target=browserstack "-Dmobile.android.device=Google Pixel 7"
```

What you should see: `vanguard-mobile  1  0  0`, `BUILD SUCCESS`.

**Which tag do I use?**

| You test | Tags | What happens |
|---|---|---|
| An installed app | `@mobile @native` | Appium installs and opens the app |
| An app with web pages inside | `@mobile @hybrid` | Same, plus the `I switch to the web view` step |
| A website in the phone's browser | `@mobile @mobileweb` | Chrome (Android) or Safari (iOS), no app. You can reuse the web Page Objects! |
| Only one platform | add `@android` or `@ios` | Otherwise `-Dmobile.platform` decides |

✅ **Check yourself**
- [ ] I wrote ONE Screen Object for both platforms.
- [ ] I used accessibility ids where possible.
- [ ] The dry run passes.

---

## Recipe 4: a new load test

> ⚠️ **Never load-test a system your team does not own.** Not public websites, not partner APIs, not "just a little bit".
> Load tests can take services down, and that can have legal consequences.
> By default Vanguard sends load to its own local fake server (WireMock).

**Goal:** check that the users endpoint stays fast under load.
You don't need any Java: `When I run the load test` makes every virtual user send GET requests to the endpoint you name.

Create `vanguard-load/src/test/resources/features/users_load.feature`:

```gherkin
# Run it alone : mvn verify -pl vanguard-load,vanguard-reporting -am -Dcucumber.filter.tags="@users-load"
@load @users-load
Feature: Users API performance

  @TEST_QA-310
  Scenario: Users API answers quickly under load
    Given a load profile "users-read" against the "users" endpoint:
      | users | rampUpSeconds | durationSeconds | thinkTimeMs |
      | 30    | 15            | 60              | 300         |
    When I run the load test
    Then the error rate is below 1 percent
    And the 95th percentile response time is below 500 ms
    And at least 10 requests were sent
```

What the table means:

| Column | Meaning |
|---|---|
| `users` | How many virtual users are active at the same time at full load |
| `rampUpSeconds` | How long it takes to go from 0 to `users` (a gentle start) |
| `durationSeconds` | How long full load is held after the ramp-up |
| `thinkTimeMs` | Pause between one user's requests (real people don't click non-stop) |

Endpoint names (`"health"`, `"products"`, `"users"`) are defined in `LoadEndpoints.java`. You can also write a path such as `"/health"`.

Run it:

```bash
mvn verify -pl vanguard-load,vanguard-reporting -am -Dcucumber.filter.tags="@users-load"
```

What you should see (by default the profile is capped to a quick "smoke" run):

```
LoadSteps - Smoke load profile: capped to 5 users / 8s (run with -Dload.profile=full for the real load)
...
vanguard-load                 1        0        0
[INFO] BUILD SUCCESS
```

Run the real load with `-Dload.profile=full`.

**Reading the results.** In the Allure report, the scenario has a **Load test results** table: requests, error rate and response times.
It also has a link to Gatling's full HTML report, with charts of response time over time.
The report is in `vanguard-load/target/gatling/<name>-<time>/`.

| Number | Means | Typical SLA |
|---|---|---|
| Error rate | % of requests that failed | below 1 % |
| p95 | 95 % of requests were faster than this | depends on the API: often 300-800 ms |
| Mean | average response time | less useful than p95; slow outliers hide in it |
| Throughput | requests per second handled | at least the expected peak traffic |

**Need a multi-step journey** (e.g. list items, then open one)? Copy `templates/load/ExampleSimulation.java`, describe one user's journey, and use its class name in the `When` step.

✅ **Check yourself**
- [ ] My load test targets WireMock or a system my team owns.
- [ ] I chose SLAs with my team, not at random.

---

## Recipe 5: share data between steps

Steps often need data from earlier steps: an id you created, a price you read. Vanguard has exactly one place for this: **`ScenarioContext`**.
Every scenario gets a fresh, empty one, so scenarios never see each other's data, even when they run in parallel.

### In Java: typed keys

```java
// Step A stores
context.set(ContextKey.CREATED_USER, createdUser);

// Step B (same scenario) reads, with its type
UserDto user = context.get(ContextKey.CREATED_USER, UserDto.class);
```

Need a new key? Add one line to `vanguard-core/src/main/java/com/vanguard/core/context/ContextKey.java`, with a comment saying what it holds.

If you read a key that was never set, you get an error that tells you what is missing:

```
No value for ContextKey.CREATED_USER in ScenarioContext. Available keys: [SCENARIO_NAME, LAST_RESPONSE].
Did an earlier step forget to call context.set(ContextKey.CREATED_USER, ...)?
```

### In Gherkin: named values

```gherkin
Then I save the response field "id" as "userId"
When I send a PUT request to "/users/{userId}" with body:
```

`{userId}` is replaced in every ready-made step.

### Across layers: an E2E journey

**Goal:** check that the web shop's detail page shows the same price as the Catalog API, for the most expensive product.
This builds on the `ProductDetailsPage` from [Recipe 1](#recipe-1-a-new-web-test).

Create `vanguard-e2e/src/test/java/com/vanguard/e2e/steps/ProductDetailsJourneySteps.java`:

```java
package com.vanguard.e2e.steps;

import com.vanguard.api.dto.ProductDto;
import com.vanguard.core.context.ContextKey;
import com.vanguard.core.context.ScenarioContext;
import com.vanguard.web.pages.ProductDetailsPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDetailsJourneySteps {

    private final ProductDetailsPage details;
    private final ScenarioContext context;

    public ProductDetailsJourneySteps(ProductDetailsPage details, ScenarioContext context) {
        this.details = details;
        this.context = context;
    }

    @When("I open the details of the most expensive catalog product")
    public void iOpenTheMostExpensiveProduct() {
        @SuppressWarnings("unchecked")
        List<ProductDto> catalog = context.get(ContextKey.CATALOG, List.class);   // stored by an earlier step
        ProductDto priciest = catalog.stream().max(Comparator.comparing(ProductDto::price)).orElseThrow();
        context.set(ContextKey.SELECTED_PRODUCT, priciest);                       // for the Then step
        details.openFromProductList(priciest.name());
    }

    @Then("the details page shows the catalog price")
    public void theDetailsPageShowsTheCatalogPrice() {
        ProductDto expected = context.get(ContextKey.SELECTED_PRODUCT, ProductDto.class);
        assertThat(details.name()).isEqualTo(expected.name());
        assertThat(details.price()).isEqualByComparingTo(expected.price());
    }
}
```

Create `vanguard-e2e/src/test/resources/features/product_details_journey.feature`:

```gherkin
# Run it alone : mvn verify -pl vanguard-e2e,vanguard-reporting -am -Dcucumber.filter.tags="@details-journey"
@e2e @web @details-journey
Feature: Product details match the catalog

  @TEST_QA-410
  Scenario: The most expensive product shows its catalog price
    Given the product catalog from the Catalog API
    And I am logged in to the web shop as "standard_user"
    When I open the details of the most expensive catalog product
    Then the details page shows the catalog price
```

The two `Given` steps already exist in `CatalogJourneySteps`. One of them stores the catalog under `ContextKey.CATALOG`. Run it:

```bash
mvn verify -pl vanguard-e2e,vanguard-reporting -am -Dcucumber.filter.tags="@details-journey"
```

What you should see: `vanguard-e2e  1  0  0`, `BUILD SUCCESS`.

✅ **Check yourself**
- [ ] Data moves between steps ONLY through `ScenarioContext`.
- [ ] I reused clients and pages from other modules instead of copying them.

---

## Recipe 6: visual checks and Jira Xray links

### Visual checks (Applitools)

A visual check takes a screenshot and compares it with an approved *baseline*.
Add one line to a web scenario:

```gherkin
    Then the "Products" page matches its visual baseline
```

Variants: `... matches its visual baseline ignoring prices` (ignores changing prices) and `... page layout matches its visual baseline` (compares structure, not exact pixels).
For mobile: `Then the "Product catalog" screen matches its visual baseline`. Tag the scenario `@visual`.

Without an Applitools key the check is **skipped with a warning** and the test still passes. To run it for real:

```bash
# Windows PowerShell:  $env:APPLITOOLS_API_KEY="your-key"
# Mac/Linux:           export APPLITOOLS_API_KEY=your-key
mvn verify -pl vanguard-web,vanguard-reporting -am -Dvisual.enabled=true -Dcucumber.filter.tags="@visual"
```

- **First run:** the baseline is saved and the test passes. Open the link in the log once and check that it looks right.
- **Later runs:** differences fail the scenario, with a link. In Applitools, click 👍 to accept the change as the new baseline, or 👎 to reject it (a real bug).
- `-Dvisual.runner=ufg` (the default) renders desktop Chrome **and** a phone in the cloud from one run. `-Dvisual.runner=classic` checks only your local browser.

### Linking scenarios to Jira (Xray)

1. In Jira, create a **Test** issue (e.g. `QA-123`), or use an existing one.
2. Tag the scenario `@TEST_QA-123`.
3. Run with the upload enabled (usually in CI, where the secrets are set):

```bash
mvn verify -Dxray.upload.enabled=true
```

What you should see: `Xray Test Execution: QA-456`. That new Jira issue lists every linked test with pass/fail.
Retried scenarios are uploaded once, with their final result. What happens to scenarios *without* a `@TEST_` tag depends on your Xray project settings.

---

## Golden Rules: what NEVER to do

Each rule has a reason. When you break one, tests become flaky, slow or impossible to maintain.

### 1. Never use `Thread.sleep()`

```java
// ❌ BAD: too slow when the page is fast, still fails when it is slower than 3 s
Thread.sleep(3000);
driver.findElement(By.id("checkout")).click();

// ✅ GOOD: waits only as long as needed (up to 10 s), with a clear error message
click(CHECKOUT);   // inside a Page Object
```

### 2. Never call `driver.findElement()` in step definitions

```java
// ❌ BAD: locators spread across steps; one UI change breaks 20 files
@When("I check out")
public void iCheckOut() { driver.findElement(By.id("checkout")).click(); }

// ✅ GOOD: steps talk to Page Objects; the locator lives in exactly one place
@When("I check out")
public void iCheckOut() { cart.checkout(); }
```

### 3. Never use `static` fields to share data

```java
// ❌ BAD: scenarios run in parallel, so another scenario overwrites this value
private static String createdUserId;

// ✅ GOOD: every scenario has its own ScenarioContext
context.set(ContextKey.CREATED_USER, user);
```

### 4. Never create Page Objects, Screen Objects or clients with `new`

```java
// ❌ BAD: bypasses Cucumber; you lose the shared ScenarioContext and the managed driver
LoginPage login = new LoginPage();

// ✅ GOOD: ask for it in the constructor; Cucumber creates and shares it
public LoginSteps(LoginPage login) { this.login = login; }
```

### 5. Never hard-code URLs or passwords

```java
// ❌ BAD: breaks on every other environment, and leaks secrets into Git
open("https://qa.mycompany.com/login");
type(PASSWORD, "Sup3rSecret!");

// ✅ GOOD: URLs come from config; secrets come from environment variables
open("/login");                                   // base URL comes from ui.base.url
type(PASSWORD, ConfigProvider.get().uiPassword());
```

### 6. Never write UI details in Gherkin

```gherkin
# ❌ BAD: breaks when the button id changes; nobody in the business can read it
When I click "#add-to-cart-sauce-labs-backpack"

# ✅ GOOD: describes what the user wants
When I add the "Sauce Labs Backpack" to the cart
```

### 7. Never load-test systems you don't own

```gherkin
# ❌ BAD: can take down someone else's service, and can be illegal
Given a load profile "x" against the "https://public-site.example" endpoint:

# ✅ GOOD: the built-in fake server, or your own team's test environment
Given a load profile "x" against the "products" endpoint:
```

Stuck? Look up the exact error message in [Troubleshooting](TROUBLESHOOTING.md).
