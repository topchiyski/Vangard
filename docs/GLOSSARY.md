# Glossary

| Term | Meaning |
|---|---|
| **Accessibility id** | A label developers put on app elements (for screen readers). The most stable mobile locator, and often the same on Android and iOS. |
| **Allure** | The HTML test report. Open it with `mvn -N allure:serve`. |
| **Appium** | The tool that remote-controls phones and emulators. A small server that Vanguard starts for you. |
| **Appium Inspector** | A desktop app that shows an app's screen and the locators of every element. |
| **Assertion** | A check that fails the test when it's wrong: `assertThat(total).isEqualTo(29.99)`. Vanguard uses AssertJ. |
| **Baseline** | The approved screenshot that visual checks compare against. |
| **BrowserStack** | A cloud service with real phones, used when you have no device. |
| **Context switch** | Moving between the native part of an app and a web view inside it (hybrid apps). |
| **Cucumber** | The tool that reads feature files and runs the matching step definitions. |
| **Dependency injection** | Cucumber creates your Page Objects, clients and `ScenarioContext` and passes them to your constructor. You never write `new`. Vanguard uses PicoContainer for this. |
| **DTO / record** | A small class that only holds data, e.g. the JSON of a user. Java `record`s write the boilerplate for you. |
| **Dry run** | Cucumber checks that every step has code, without running anything. `-Dcucumber.execution.dry-run=true`. |
| **E2E (end-to-end)** | A test that goes through several layers, e.g. data from the API checked in the web shop. |
| **Emulator / simulator** | A virtual phone on your computer (Android emulator, iOS simulator). |
| **Feature file** | A `.feature` file with scenarios written in Gherkin. |
| **Flaky test** | A test that sometimes passes and sometimes fails without any code change. Usually caused by `Thread.sleep`, shared state or timing. |
| **Gatling** | The load-testing tool. Vanguard uses its Java API; no Scala needed. |
| **Gherkin** | The `Given / When / Then` language of feature files. |
| **Glue** | The Java packages where Cucumber looks for step definitions and hooks. |
| **Headless** | A browser running without a visible window (faster, needed on CI). |
| **Hook** | Code that runs automatically before or after scenarios (`@Before` / `@After`): starting browsers, taking screenshots. |
| **Hybrid app** | A native app that shows web pages inside it (web views). |
| **JSON schema** | A description of the shape of a JSON response: which fields, which types. |
| **Load test** | Many virtual users at once, to measure speed and errors under pressure. |
| **Locator** | How to find an element: `By.id("login-button")`, a CSS selector, an accessibility id. |
| **p95 (95th percentile)** | 95 % of requests were faster than this value. Better than the average for spotting slowness. |
| **Page Object** | A class that represents one web page: its locators plus one method per user action. |
| **Ramp-up** | Gradually increasing the number of load-test users instead of starting all at once. |
| **Retry** | Vanguard re-runs a failed scenario once (`retry.max.count`). A test that only passes on retry is flaky; fix it. |
| **ScenarioContext** | The per-scenario storage for passing data between steps. |
| **Screen Object** | The mobile version of a Page Object: one class works for Android and iOS. |
| **Selenium** | The tool that remote-controls web browsers. |
| **SLA** | A performance promise, e.g. "p95 below 500 ms, errors below 1 %". |
| **Smoke test** | A small set of the most important checks (`@smoke`). |
| **Step definition** | A Java method linked to a Gherkin line by its text. |
| **Stub** | A fake API answer served by WireMock. |
| **Think time** | The pause a virtual user makes between requests in a load test. |
| **Throughput** | Requests handled per second. |
| **UFG (Ultrafast Grid)** | Applitools renders your page in many browsers and devices in the cloud from one local run. |
| **Visual test** | A screenshot comparison against a baseline (Applitools). |
| **Web view** | A browser component embedded inside a mobile app. |
| **WireMock** | A local fake API server, so sample tests work without the real system. |
| **Xray** | The Jira plugin that stores test results; scenarios link to it with `@TEST_<KEY>` tags. |
