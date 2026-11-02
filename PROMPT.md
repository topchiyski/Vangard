# Role & Objective
You are a Staff Test Automation Architect. Build **Vanguard**, a production-grade BDD test automation platform, from scratch in the current directory. It covers **API, Web, Mobile (Android & iOS), Load, and cross-layer E2E** testing.

Vanguard is for **Junior QA Engineers**. Every test type is written in **Gherkin**. Every test type also shares the same core services:
- configuration
- `ScenarioContext`
- hooks
- retry
- Allure reporting
- Jira Xray upload
- WireMock

Hide all complex infrastructure behind base classes, Cucumber hooks and configuration. That includes thread-safety, driver/Appium lifecycle, Gatling execution, report attachments, the visual-testing lifecycle and the Xray upload. Juniors only ever write:
- feature files
- Page Objects / Screen Objects
- API clients
- load profiles
- Step Definitions

**No Scala anywhere.** Load tests use the Gatling **Java DSL**, with no Scala sources and no Scala compiler plugin. The Scala runtime jars that Gatling pulls in transitively are acceptable.

---

# Junior Experience: The #1 Requirement
Junior-friendliness is a **hard acceptance criterion**, not a nice-to-have. Every design decision must favour "a junior can add a new test by copying an example" over cleverness.

## 1. Golden examples (one reference example per domain)
Each module has one **reference example** that juniors copy. It is the best-written code in the project:
- web, API, mobile native, mobile hybrid, mobile web, load, E2E and visual.

**Each reference example:**
- has a feature file with a short comment header covering:
  - what it tests;
  - which Page/Screen Objects, clients or simulations it uses;
  - how to run it alone (the exact command).
- uses declarative business language in Gherkin:
  - **Good:** `When I add the "Sauce Labs Backpack" to the cart`
  - **Bad:** `When I click "#add-to-cart-sauce-labs-backpack"`
- has Page/Screen Objects, clients, steps and simulations with:
  - short Javadoc on every public method;
  - `// WHY:` comments on anything non-obvious.
- is named `*Example*` or tagged `@example`, so juniors can find it.
- passes out of the box.

## 2. Copy-paste templates
- Put them in `templates/<domain>/`, one starter per domain:
  - a `.feature`;
  - a Page Object, Screen Object, API client, `record` DTO or load simulation;
  - the step definitions.
- Templates hold `TODO:` markers that say exactly what to replace.
- Templates compile, but they are excluded from test runs.

## 3. Shared common step library (no code needed for common checks)
Juniors can write many scenarios **without writing any Java**, using ready-made steps. Every step is documented in `docs/STEP_CATALOGUE.md`, with one example per step.
- **API** (`vanguard-api`):
  - `When I send a GET request to "/users/1"`
  - `When I send a POST request to "/users" with body:` + a docstring
  - `Then the response status is 200`
  - `And the response field "name" equals "Leanne"`
  - `And the response matches the schema "user-schema.json"`
  - `And the response time is below 1000 ms`
  - `And I save the response field "id" as "userId"`, then reuse it as `{userId}` in later steps
- **Web** (`vanguard-web`; navigation and assertions only, with business actions kept in Page Objects):
  - `Given I open the "/inventory.html" page`
  - `Then the page URL contains "checkout"`
  - `Then the page title is "Swag Labs"`
- **Mobile:**
  - `Given the app is launched`
  - `When I switch to the web view`
  - `When I rotate the device to landscape`
- **Load:** the SLA steps (error rate, percentiles, throughput).
- **Context:** `And I remember "<value>" as "<name>"`, with `{name}` placeholders resolved in every step parameter.

## 4. Errors that teach
Framework exceptions say **what went wrong and how to fix it**. Examples:
- **Missing context key:**
  > `No value for ContextKey.CREATED_USER in ScenarioContext. Available keys: [LAST_RESPONSE]. Did an earlier step forget to call context.set(...)? See docs/JUNIOR_ONBOARDING_GUIDE.md#recipe-5`
- **Wait timeout:**
  > `LoginPage: element By.id("user-name") not visible after 10s on https://… — check the locator in LoginPage or whether the page loaded.`
- **Appium not reachable:**
  > `Appium not reachable at http://127.0.0.1:4723 — run 'appium' or see docs/MOBILE_SETUP.md#appium`
- **Undefined step:** keep Cucumber's snippet output enabled, so juniors get a ready-to-paste method.

## 5. Conventions in one place
- Naming conventions for features, scenarios, Page Objects/Screen Objects (`*Page`, `*Screen`), clients (`*Client`), steps (`*Steps`) and simulations (`*Simulation`).
- One step-definition class per business area, not per feature.
- A **tag catalogue**: `@api @web @mobile @android @ios @native @hybrid @mobileweb @load @e2e @visual @smoke @wip @example @TEST_<KEY>`.

---

# Tech Stack & Versions
**Version policy:** For every dependency and plugin, use the **latest stable (non-alpha/beta/RC) release on Maven Central at build time**. Pin exact versions once, in the parent pom's `<properties>` / `<dependencyManagement>` / `<pluginManagement>`. The minimum versions listed are floors. If two libraries conflict at their latest versions, step back to the newest compatible version and leave a comment in the pom explaining why.

| Area | Choice |
|---|---|
| Language | Java 21 (`maven.compiler.release=21`); use `record` DTOs, text blocks and pattern matching |
| Build | Maven **multi-module** reactor; Surefire runs TestNG |
| BDD | Cucumber JVM ≥7.18 (`cucumber-java`, `cucumber-testng`, `cucumber-picocontainer`) + TestNG ≥7.10 |
| Web | Selenium 4 (≥4.25) with Selenium Manager; Chrome only (desktop + `mobileEmulation`) |
| Mobile | Appium **java-client** (≥9) against an Appium 2 server; `UiAutomator2Options` (Android) and `XCUITestOptions` (iOS) |
| API | RestAssured ≥5.5, `json-schema-validator`, Jackson ≥2.17, `allure-rest-assured` |
| Load | Gatling Java DSL (`io.gatling:gatling-core-java`, `gatling-http-java`, `io.gatling.highcharts:gatling-charts-highcharts`) |
| Mock server | WireMock (`org.wiremock:wiremock-standalone` ≥3.9) |
| Visual | Applitools `eyes-selenium-java5` (web) + `eyes-appium-java5` (native mobile) |
| Cloud devices | BrowserStack App Automate / Automate |
| Reporting | Allure (`allure-cucumber7-jvm`, `allure-rest-assured`; **do NOT add `allure-testng`**, because it duplicates every scenario); AspectJ weaver in the Surefire `argLine`; `allure-maven` plugin |
| Config | Owner `org.aeonbits.owner:owner` 1.0.12 |
| Utilities | AssertJ, Lombok (in `annotationProcessorPaths`), SLF4J + Logback |

---

# Project Structure (Maven multi-module)
```
vanguard/                         parent pom (packaging=pom): versions, plugins, shared Surefire config
├── vanguard-core/                src/main: shared framework (no tests of its own except unit tests)
├── vanguard-api/                 src/main: API clients + record DTOs + schemas   | src/test: API features
├── vanguard-web/                 src/main: BasePage + Page Objects + Chrome factory | src/test: web features
├── vanguard-mobile/              src/main: Appium factory + Screen Objects       | src/test: mobile features
├── vanguard-load/                src/main: Gatling simulations + load DSL        | src/test: load features
├── vanguard-e2e/                 depends on api + web (+ mobile)                 | src/test: cross-layer features
├── vanguard-reporting/           last in reactor: merges results, Allure env, Xray upload, final verdict
├── .github/workflows/
├── docs/
└── README.md
```

**Rules:**
- Reusable code (Page Objects, Screen Objects, API clients, DTOs, simulations) lives in each module's **`src/main`**, so `vanguard-e2e` can depend on it. Features, step definitions and runners live in **`src/test`**.
- Base package is `com.vanguard.<module>`. Every module's Cucumber runner includes the glue package `com.vanguard.core.hooks` plus its own steps.
- Feature files go in `src/test/resources/features/` of each module.
- Each module writes `target/cucumber-reports/cucumber.json` and `target/allure-results/`.

---

# vanguard-core (shared by every module)

## 1. Configuration (`Owner`)
- `FrameworkConfig` loads `classpath:config/${env}.properties`, which lives in core and covers the `qa`, `staging` and `prod` envs.
- System properties override file values. Secrets come from environment variables when the property is blank.
- `ConfigProvider.get()` is a thread-safe singleton accessor.
- Derived values (e.g. "visual effectively enabled") are computed in code, not in Owner.

| Key | Default | Notes |
|---|---|---|
| `env` | `qa` | |
| `ui.base.url` | `https://www.saucedemo.com` | |
| `api.base.url` | *(empty)* | Empty means use the embedded WireMock URL, resolved by `ApiEndpoints.baseUrl()` |
| `load.base.url` | *(empty)* | Empty means WireMock. **Never** point it at a third-party public site |
| `threads` | `4` | Parallel scenarios for api/web/e2e |
| `retry.max.count` | `1` | |
| `browser.mode` | `desktop` | `desktop` or `mobile` (Chrome emulation) |
| `browser.headless` | `true` | |
| `emulation.device.name` | `Pixel 7` | Falls back to explicit metrics if Chrome rejects the name |
| `mobile.platform` | `android` | `android` or `ios` |
| `mobile.target` | `local` | `local` or `browserstack` |
| `mobile.appium.url` | `http://127.0.0.1:4723` | Local server |
| `mobile.appium.autostart` | `true` | Start Appium via `AppiumServiceBuilder` if nothing answers at the URL |
| `mobile.android.device` / `mobile.android.version` | `emulator-5554` / *(empty)* | |
| `mobile.ios.device` / `mobile.ios.version` | `iPhone 15` / `17.5` | |
| `mobile.android.app` / `mobile.ios.app` | `apps/android/mda.apk` / `apps/ios/mda.app.zip` | Local path **or** `bs://<id>` |
| `browserstack.username` / `browserstack.access.key` | *(env `BROWSERSTACK_USERNAME` / `BROWSERSTACK_ACCESS_KEY`)* | |
| `browserstack.build.name` | `Vanguard - ${env}` | |
| `load.profile` | `smoke` | `smoke` or `full`; caps users and duration (see Load) |
| `visual.enabled` | `false` | Effective only if `APPLITOOLS_API_KEY` is also set |
| `visual.runner` | `ufg` | `ufg` or `classic` (native mobile always uses classic) |
| `visual.batch.name` | `Vanguard - ${env}` | |
| `xray.upload.enabled` | `false` | |
| `xray.mode` | `cloud` | `cloud` or `datacenter` |
| `xray.base.url` | `https://xray.cloud.getxray.app` | |
| `xray.client.id` / `xray.client.secret` / `xray.token` | *(env `XRAY_CLIENT_ID` / `XRAY_CLIENT_SECRET` / `XRAY_TOKEN`)* | |
| `xray.project.key` | `QA` | |

## 2. ScenarioContext & Wiring
- **`ScenarioContext`:** One instance per scenario, provided by PicoContainer.
  - API: `set(ContextKey, Object)`, `get(ContextKey, Class<T>)` and `contains(ContextKey)`.
  - `ContextKey` is an enum shared by all modules (e.g. `LAST_RESPONSE`, `CREATED_USER`, `LOAD_RESULT`, `ORDER_TOTAL`).
  - `get` throws a clear message when the key is missing or the value has the wrong type.
- **Constructor injection:** Page Objects, Screen Objects, API clients, load runners and Step Definitions are all constructor-injected by PicoContainer. Juniors never write `new`.
- **Static variables:** Juniors never use `static` state.

## 3. Driver Registry (shared by web and mobile)
- `DriverRegistry` holds a `ThreadLocal<WebDriver>` (Appium drivers are `WebDriver`s), so failure hooks work the same for web and mobile.
- Drivers are created **lazily** by a pluggable `DriverFactory`: web supplies `ChromeDriverFactory` and mobile supplies `AppiumDriverFactory`. API and load scenarios therefore never start a browser.
- The driver quits and the ThreadLocal is removed in `@After`.

## 4. Shared Hooks (`com.vanguard.core.hooks`)
- **`@BeforeAll`:** start WireMock once per JVM.
- **`@After` for any scenario with a driver that failed:** attach to Allure:
  - a PNG screenshot;
  - the current URL (web) or the activity/bundle and page source (mobile);
  - the browser console logs (web only).
- **`@After`:** close Eyes if it was opened; quit the driver.

## 5. WireMock
- `WireMockManager` starts one server per JVM on a dynamic port, lazily and thread-safely. It uses the `response-template` extension.
- Stubs live in core's `src/main/resources/wiremock/` and are **stateless** canned responses:
  - **Users:** `GET /users/{1..3}` → 200, `GET /users/999` → 404, `POST /users` → 201 echo + id, `POST /users` without email → 400, `PUT /users/{id}` → 200 echo, `DELETE /users/{id}` → 204.
  - **Catalog:** `GET /catalog/products` returns the 6 SauceDemo products with names and prices, used by E2E.
  - **Load:** `GET /health` and `GET /products/{id}` with a small configurable `fixedDelayMilliseconds`, so load tests produce realistic numbers.

## 6. Retry
- `RetryAnalyzer` (`IRetryAnalyzer`) plus `RetryTransformer` (`IAnnotationTransformer`), registered **only** in each module's `testng.xml`.
- Earlier attempts are marked SKIPPED, and the scenario passes if the last attempt passes.
- **Load scenarios are never retried:** the transformer skips the load runner.

## 7. Visual (Applitools)
- `EyesManager` uses a `ThreadLocal<Eyes>` and a suite-scoped runner. Each module has one batch named `visual.batch.name`; results from all modules land in that one batch.
- **Web:** `VisualGridRunner` (Chrome 1920x1080 + Pixel 7 emulation, rendered in the cloud) when `visual.runner=ufg`, otherwise `ClassicRunner`.
- **Native mobile:** `eyes-appium-java5` with `ClassicRunner`.
- **Pass/fail policy:**
  - new baselines pass;
  - visual diffs fail the scenario;
  - when visual is not effectively enabled, the visual steps log `WARN Visual check skipped: <reason>` and pass.

## 8. Unit tests in core
- `ScenarioContext`
- config resolution
- retry
- `XrayResultsExporter`, using its own WireMock with constructor-injected settings. It asserts on:
  - Cloud authentication and import with the Bearer token;
  - deduplication of retried scenarios;
  - Data Center mode hitting `/rest/raven/2.0/import/execution/cucumber`.

---

# vanguard-api
- **`BaseApiClient`:**
  - a RestAssured spec with the `AllureRestAssured` filter, JSON content type, the `ApiEndpoints.baseUrl()` base URL and `log().ifValidationFails()`;
  - `get`, `post`, `put` and `delete` store the response in `ScenarioContext` under `LAST_RESPONSE`.
- **Sample:** `UserClient`, `CatalogClient`, `record UserDto`, `record ProductDto` and `schemas/user-schema.json`.
- **`features/user_crud_and_schema.feature`** (`@api`):
  - CRUD;
  - schema validation;
  - a `Scenario Outline` reading users 1–3;
  - a 404 for an unknown user and a 400 for a missing email;
  - record (de)serialization;
  - `@TEST_QA-101` / `@TEST_QA-102` tags.

---

# vanguard-web
- **`BasePage`:**
  - 10-second explicit waits;
  - `click`, `type` (clears first), `getText`, `isDisplayed` (returns false instead of throwing), `waitForUrlContains`, `currentUrl` and `executeScript`;
  - juniors never call `driver.findElement()` or `Thread.sleep()`.
- **`ChromeDriverFactory`:**
  - **Mode:** desktop 1920x1080, or `mobileEmulation`. Precedence: tag `@mobile-emulation`/`@desktop` → `-Dbrowser.mode` → default.
  - **Headless:** `--headless=new` when configured.
  - **Chrome prefs:** `credentials_enable_service=false`, `profile.password_manager_enabled=false` and `profile.password_manager_leak_detection=false`, which stop the SauceDemo password popup.
  - **Logging:** `goog:loggingPrefs` = `{browser: ALL}`.
  - **Device fallback:** if Chrome rejects the device name, use a built-in `deviceMetrics` + `userAgent` table (at least `Pixel 7` and `iPhone 14 Pro Max`).
- **Page Objects:** `LoginPage`, `InventoryPage`, `CartPage` and `CheckoutPage`.
- **Features:**
  - `web_saucedemo_e2e.feature` (`@web @desktop`): login, add 2 items, badge = 2, checkout, plus the `locked_out_user` error;
  - `web_responsive.feature` (`@web @mobile-emulation`): assert `window.innerWidth` = 412, then a cart flow;
  - `web_visual.feature` (`@web @visual`): `fully().strict()`, `fully().ignore(By.cssSelector(".inventory_item_price"))` and `fully().layout()`.

---

# vanguard-mobile (Android + iOS, native / hybrid / mobile web)
## Sample apps and sites
- **Native & hybrid:** the public **Sauce Labs "My Demo App"**:
  - Android `.apk` from `saucelabs/my-demo-app-android` releases;
  - iOS simulator `.app.zip` from `saucelabs/my-demo-app-ios` releases.
  - Provide `scripts/download-sample-apps.ps1` and `.sh` that fetch them into `apps/`, which is git-ignored.
- **Mobile web:** saucedemo.com in **Chrome on Android** and **Safari on iOS**.

## Driver
- `AppiumDriverFactory` builds `UiAutomator2Options` or `XCUITestOptions` from config.
- **Platform precedence:** tag `@android`/`@ios` → `-Dmobile.platform` → default.
- **App type:**
  - `@native` / `@hybrid` install the app;
  - `@mobileweb` sets `browserName` (Chrome or Safari) and installs no app.
- **`local` target:** connect to `mobile.appium.url`. If nothing answers there and `mobile.appium.autostart=true`, start Appium with `AppiumServiceBuilder` and stop it at JVM shutdown. If the Appium/Node/driver prerequisites are missing, fail fast with a clear message that names the setup doc.
- **`browserstack` target:** use the `https://hub-cloud.browserstack.com/wd/hub` hub with `bstack:options` (user, key, build name, session name = scenario name, device and OS version).
  - Include `BrowserStackAppUploader`, built on `java.net.http.HttpClient`. It uploads a local app to `https://api-cloud.browserstack.com/app-automate/upload` and caches the returned `bs://` id for the run.
  - Mark the session passed or failed via `browserstack_executor` in `@After`.

## Screen Objects
- **`BaseScreen`:**
  - explicit waits;
  - `tap`, `type`, `getText`, `isDisplayed`, `swipe(Direction)` (W3C Actions) and `scrollTo(text)`;
  - `hideKeyboard`;
  - `switchToWebView()` / `switchToNative()`, which wait for the WEBVIEW context;
  - `executeScript`.
- **Locators:** `record MobileLocator(By android, By ios)` with `MobileLocator.accessibilityId("id")` as the preferred shortcut. `BaseScreen` picks the right `By` for the current platform, so juniors write **one Screen Object for both platforms**.
- **Screens:** `ProductsScreen`, `ProductDetailsScreen`, `CartScreen`, `LoginScreen`, `MenuScreen` and `WebViewScreen`.

## Features (each runs on Android and iOS by selecting the platform)
- `native_shopping.feature` (`@mobile @native`): log in through the menu, add a product, verify the cart count, then swipe and scroll on the product list.
- `hybrid_webview.feature` (`@mobile @hybrid`): open the WebView screen, load a URL, switch context, assert web content, switch back.
- `mobile_web_saucedemo.feature` (`@mobile @mobileweb`): log in and add to cart on saucedemo in mobile Chrome or Safari.
- `mobile_visual.feature` (`@mobile @native @visual`): an Eyes check of the products screen.

## Execution
- Mobile scenarios run with **thread count 1** per device by default.
- The module's tests run only with the Maven profile `-Pmobile`. Without it, Surefire skips them and logs a one-line notice, so `mvn clean verify` stays green on machines without devices.

---

# vanguard-load (Gatling Java DSL, driven by Gherkin)
## Design
- Simulations are Java classes in `src/main/java`, for example `ApiBrowseSimulation extends Simulation`.
- Simulations read their parameters from a `LoadProfile` record (users, ramp, duration, endpoint, pauses, SLA thresholds). The step definition hands the profile over as JSON in a system property, because Gatling instantiates simulation classes by name.
- **`GatlingRunner`** runs a simulation **in a forked JVM** via Gatling's programmatic main class, with an isolated `results` folder per scenario. It then parses Gatling's generated stats (`js/stats.json` or equivalent for the chosen version) into a `record LoadResult`:
  - total requests;
  - failed %;
  - mean, p50, p95 and p99 response times;
  - requests per second.

  Verify the entry point and stats format against the Gatling version actually resolved, and document them in a code comment.
- **Load profile caps:** `load.profile=smoke` caps every profile at 5 users and 15 seconds, whatever the feature says. `full` uses the feature's values.
- **Reuse from core and API:**
  - config (`load.base.url` → WireMock by default);
  - `ScenarioContext` (stores `LOAD_RESULT`);
  - record DTOs from `vanguard-api` for request bodies (serialized with Jackson);
  - Allure and Xray.
- **Allure:** attach a Markdown/HTML table of the `LoadResult`, plus the zipped Gatling HTML report and a link to its `index.html`.
- The load runner is **serial** (no parallel scenarios) and **never retried**.

## Gherkin style (sample `features/api_load.feature`, `@load`)
```gherkin
@load @TEST_QA-201
Scenario: Product API holds steady under moderate load
  Given a load profile "product-browse" against the "products" endpoint
    | users | rampUpSeconds | durationSeconds |
    | 50    | 30            | 120             |
  When I run the "ApiBrowseSimulation" load test
  Then the error rate is below 1 percent
  And the 95th percentile response time is below 500 ms
  And the throughput is at least 20 requests per second
```
Also add a `@load @smoke` scenario against `/health`, and a `Scenario Outline` that ramps 10 → 25 → 50 users.

---

# vanguard-e2e (cross-layer journeys)
- Depends on `vanguard-api` and `vanguard-web`, plus `vanguard-mobile` under `-Pmobile`. It reuses their Page Objects, Screen Objects and clients through injection; no copies.
- **`features/catalog_to_checkout.feature`** (`@e2e`):
  1. **Given** the catalog from `CatalogClient` (WireMock).
  2. **When** I buy the cheapest catalog product in the web shop.
  3. **Then** the checkout item total equals the API price, and the product name matches.

  Data passes between layers only through `ScenarioContext`.
- **Optional `@e2e @mobile` scenario:** the same product data is checked in the native app's product details. It runs only under `-Pmobile`.

---

# vanguard-reporting (one combined run)
- This module is last in the reactor and bound to the `verify` phase. Every test module sets Surefire `testFailureIgnore=true`, so a failure doesn't stop the reactor.
- **`ReportAggregator`** (a `main` class, also runnable standalone with `--input <dir>` so CI can point it at downloaded artifacts):
  1. Collect every module's `target/allure-results` into `target/allure-results` at the root, and write `environment.properties` there with:
     - Java version
     - OS
     - env
     - browser mode
     - mobile platform and target
     - load profile
     - visual (effective)
     - Xray upload
  2. Merge all `cucumber.json` files into one `target/cucumber-reports/cucumber.json`, **deduplicating retried scenarios** (keep the last attempt per feature URI + scenario id + line).
  3. If `xray.upload.enabled=true`, call `XrayResultsExporter` from core to create **one Test Execution** for the whole run.
  4. Print a summary of passed, failed and skipped scenarios per module. **Fail the build** if any scenario failed.
- **`XrayResultsExporter`** (core; `java.net.http.HttpClient` only):
  - **Cloud:**
    1. `POST {xray.base.url}/api/v2/authenticate` → the token, as a quoted JSON string.
    2. `POST {xray.base.url}/api/v2/import/execution/cucumber` with Bearer auth.
  - **Data Center:** `POST {xray.base.url}/rest/raven/2.0/import/execution/cucumber` with a Bearer PAT.
  - **Timeouts:** 10s connect, 60s request.
  - **Errors:** non-2xx responses log the status and body. An upload failure logs ERROR but never by itself fails the build.
  - **Success:** log `Xray Test Execution: <KEY>`.
- **Allure:** `mvn allure:serve` from the root serves the combined report.

---

# CI/CD: `.github/workflows/test-execution.yml`
- **Triggers:**
  - `push` to `main`;
  - `pull_request`;
  - `schedule` (nightly);
  - `workflow_dispatch` with the inputs below.

  | Input | Values | Default |
  |---|---|---|
  | `environment` | qa / staging / prod | qa |
  | `suites` | multi-choice of api, web, mobile-android, mobile-ios, load, e2e | all except mobile-ios |
  | `cucumber_tags` | free text | `not @wip` |
  | `browser_mode` | desktop / mobile | desktop |
  | `mobile_target` | local / browserstack | local |
  | `load_profile` | smoke / full | smoke |
  | `upload_to_xray` | true / false | false |
- **Jobs:**
  - **`core-api-web-e2e`:** `ubuntu-latest`, Temurin 21 with Maven cache, Chrome via `browser-actions/setup-chrome`.
  - **`load`:** `ubuntu-latest`. `smoke` on push and PR; `full` on nightly or manual dispatch.
  - **`mobile-android`:** `ubuntu-latest` with KVM and `reactivecircus/android-emulator-runner`; installs Node, Appium 2 and the `uiautomator2` driver; downloads the sample apps. Runs on nightly and dispatch, or on BrowserStack when `mobile_target=browserstack`.
  - **`mobile-ios`:** `macos-latest` with an iOS simulator, Appium 2 and the `xcuitest` driver, or BrowserStack. Runs on nightly and dispatch only.
  - **`report`** (`needs` all jobs, `if: always()`):
    1. Download every job's `allure-results` and `cucumber.json`.
    2. Run `ReportAggregator --input`.
    3. Upload to Xray if enabled.
    4. Run `mvn allure:report`.
    5. Upload the combined Allure HTML, the Gatling reports and the merged `cucumber.json` as **workflow artifacts**. There is no GitHub Pages.
- **Secrets:** `APPLITOOLS_API_KEY`, `XRAY_CLIENT_ID`, `XRAY_CLIENT_SECRET`, `BROWSERSTACK_USERNAME` and `BROWSERSTACK_ACCESS_KEY`.

---

# Junior-Friendly Documentation
1. **Root `README.md`:**
   - a Mermaid diagram of the modules and the shared core;
   - prerequisites per test type: JDK 21, Maven and Chrome for everyone; Node, Appium 2, Android Studio/SDK or Xcode for mobile;
   - a 3-minute quick start:
     - `mvn clean verify`
     - `mvn -pl vanguard-api,vanguard-reporting -am verify`
     - `-Dcucumber.filter.tags="@smoke"`
     - `-Pmobile -Dmobile.platform=ios`
     - `-Dload.profile=full`
     - `mvn allure:serve`
   - a table of every config key.
2. **A short `README.md` in each module:**
   - what it tests;
   - where the reference example and the template are;
   - how to run the module alone;
   - "where do I add…?" answers.
3. **Writing style for all guides:**
   - plain English, short sentences, and no jargon left unexplained (add a `docs/GLOSSARY.md` for terms such as Page Object, locator, glue, PicoContainer, p95 and WebView);
   - numbered steps with **complete copy-paste code** (never `...` gaps);
   - after each command, a short "What you should see" block showing the expected console output;
   - a "Check yourself" checklist at the end of each how-to.
4. **`docs/JUNIOR_ONBOARDING_GUIDE.md`:**
   - **Day 1 section:** install, run the smoke suite, open the Allure report and find a failure screenshot.
   - **Recipes:** each one walks through a **complete realistic example from empty file to green test** that matches its reference example.
     1. New web test: Feature → Page Object → Step Definition → Xray tag.
     2. New API test:
        - first with **only** common steps (no Java at all);
        - then with a client, a `record` DTO, JSON Schema validation and a new WireMock stub.
     3. New mobile test: one Screen Object with `MobileLocator` for Android + iOS; finding locators with Appium Inspector; native vs hybrid vs mobile web tags; running locally vs on BrowserStack.
     4. New load test: write the `LoadProfile` table in Gherkin, reuse an existing simulation or create one from the template, choose sensible SLA thresholds, and read the Gatling report. Warn clearly that juniors must never load-test systems they don't own.
     5. New E2E journey that reuses clients and pages across modules and passes data through `ScenarioContext`.
     6. Visual tests (web + native) and Xray linking (`@TEST_PROJ-123`).
   - **Golden Rules: What NEVER To Do.** For each rule, show a ❌ bad code snippet next to a ✅ good one, with a one-line reason. The rules forbid:
     - `Thread.sleep()`
     - `driver.findElement()` in steps
     - `static` state
     - `new PageObject()`
     - hard-coded URLs or credentials
     - UI-detail Gherkin
     - load-testing third-party sites
5. **`docs/STEP_CATALOGUE.md`:** every common step, with its parameters and an example.
6. **`docs/GHERKIN_STYLE_GUIDE.md`:**
   - declarative vs imperative steps;
   - one behaviour per scenario;
   - when to use `Background`, `Scenario Outline` and data tables;
   - good vs bad examples.
7. **`docs/TROUBLESHOOTING.md`:** an FAQ of real error messages juniors will hit (undefined step, timeouts, stale element, Chrome/driver mismatch, Appium not running, emulator not found, WireMock stub not matched, schema mismatch, Gatling SLA failed), each with its cause and fix.
8. **`docs/CHEATSHEET.md`:** one page listing every run command, tag and config override.
9. **`docs/MOBILE_SETUP.md`:**
   - Windows/Linux Android emulator setup;
   - macOS iOS simulator setup;
   - installing Appium 2 + drivers (`appium driver install uiautomator2`, `xcuitest`);
   - `appium-doctor`;
   - BrowserStack setup.

---

# Claude Code Guidance Files
Claude Code files steer future AI sessions on this repo. They are **not** human docs, and they must stay small.
- **Root `CLAUDE.md`** (≤ ~60 lines):
  - a 2–4 line identity and module map;
  - exact commands: full run, single module, single feature/tag, mobile dry run, Allure;
  - a few repo-specific conventions;
  - a verification checklist.
- **Per-module `vanguard-*/CLAUDE.md`** (≤ ~40 lines each): module-only commands, patterns and pitfalls. These load on demand.
- **`.claude/rules/*.md` with `paths:` frontmatter** for file-type conventions, e.g. `**/*.feature` → Gherkin style, `**/steps/**/*.java` → step rules.
- **`.claude/settings.json`:** deterministic `permissions.deny` for `.env*`, `**/secrets/**`, `git push --force*`, and `rm -rf` outside `target/`. Security rules belong here, not in `CLAUDE.md`.
- **Writing rules:**
  - Write in telegraphic imperative style.
  - Pair every "never" with an "instead, do Y".
  - Use at most 3 `IMPORTANT:` markers per file.
  - Leave out anything Claude can derive from the code.
  - Put no `@file` imports of large docs; reference paths to read on demand instead.
  - Give every non-obvious rule an HTML rationale comment: `<!-- YYYY-MM-DD: Triggered by … Outcome: … Prune when: … -->`.
- Write these files **after** the commands they list have been verified.

---

# Execution Instructions
Build in **seven stages**. After each stage, run the listed command and fix every compilation or test failure before moving on.
1. **Stage 1 — parent pom, `vanguard-core` (incl. unit tests), `vanguard-api`, `vanguard-reporting`.** Run `mvn clean verify`.
2. **Stage 2 — `vanguard-web`, including the visual feature with graceful skip.** Run `mvn clean verify`.
3. **Stage 3 — `vanguard-load`.** Run `mvn clean verify`, with the smoke profile against WireMock.
4. **Stage 4 — `vanguard-e2e`.** Run `mvn clean verify`.
5. **Stage 5 — `vanguard-mobile`.**
   - Run `mvn clean verify -Pmobile -Dcucumber.execution.dry-run=true` to prove that every step has glue and everything compiles.
   - If an Android emulator/device and Appium are available, also run `-Pmobile -Dmobile.platform=android` for real.
   - iOS cannot run on Windows. Validate it with the dry run and the BrowserStack configuration only.
6. **Stage 6 — CI workflow, templates, all docs.** In every earlier stage, build that module's reference example, common steps, template and teaching error messages together with the module. Do not leave them for the end.
7. **Stage 7 — Docs walkthrough (proof that the guides work).**
   1. Follow each recipe in `JUNIOR_ONBOARDING_GUIDE.md` **literally, as a junior would**, copying only the code shown in the guide.
   2. Create the recipe's test in a scratch branch or folder and run it.
   3. Wherever the guide was wrong, incomplete or unclear, fix the **guide** and re-run the recipe.
   4. Delete the scratch tests afterwards.
   5. Report which recipes were verified.

   For mobile recipes, verify as far as the environment allows (dry run).

**Done means:**
- `mvn clean verify` at the root finishes with 0 failed scenarios across core, api, web, load (smoke), e2e and reporting.
- The mobile dry run passes.
- The combined Allure results and the merged `cucumber.json` exist at the root.
- Every reference example passes, and every template compiles.
- Every common step appears in `STEP_CATALOGUE.md`.
- Every recipe was verified in Stage 7.

If tests fail **only** because of something outside the code — saucedemo.com or the internet unreachable, no mobile device or emulator, no Appium installed — do not weaken or delete the tests. Report the cause clearly, with the evidence, and finish.
