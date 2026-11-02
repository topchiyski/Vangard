# Vanguard

Java 21 Maven multi-module BDD test platform (Cucumber 7 + TestNG). Users are junior QA engineers: favour copyable, readable patterns over clever abstractions.

## Module map
- `vanguard-core`: config (Owner), `ScenarioContext`, `DriverRegistry`, shared hooks, WireMock stubs, retry, Xray exporter. Every module depends on it.
- `vanguard-api` / `-web` / `-mobile` / `-load`: reusable code in `src/main`, features + steps + runner in `src/test`.
- `vanguard-e2e`: journeys reusing api + web via injection. `templates/`: compiled starters, never run.
- `vanguard-reporting`: last in reactor; merges results, uploads Xray, fails the build on failed scenarios.

## Commands (verified)
- One module: `mvn verify -pl vanguard-api,vanguard-reporting -am`. Always include `vanguard-reporting`: test modules ignore failures, reporting decides the verdict.
- One tag/feature: add `-Dcucumber.filter.tags="@tag"`.
- Compile only: `mvn -q -DskipTests install -pl <module> -am`.
- Core unit tests: `mvn verify -pl vanguard-core`.
- Mobile without a device: `mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.execution.dry-run=true`.
- Everything: `mvn clean verify` (~3 min; mobile skipped without `-Pmobile`). Report: `mvn -N allure:serve`.

## Conventions
- IMPORTANT: Get pages, screens, clients and `ScenarioContext` through constructor injection (PicoContainer). Instead of `new` or `static` state, add a constructor parameter.
- Share data only via `ScenarioContext`: `ContextKey` (add new keys with a comment) or `remember`/`resolve` for Gherkin `{placeholders}`.
- Interact with UI only through `BasePage` / `BaseScreen` helpers; instead of `driver.findElement` or `Thread.sleep`, add a helper there.
- Mobile locators: `MobileLocator.accessibilityId(..)`, or `new MobileLocator(android, ios)` when they differ.
- Browser scenarios tagged `@web`; mobile `@mobile` + `@native|@hybrid|@mobileweb`; load `@load`.
- New features start with the What/Uses/Run-it-alone header; reference examples carry `@example`.
- Framework errors must say how to fix the problem (see `ScenarioContext.get`).
- Money as `BigDecimal`. Secrets from env vars via `ConfigProvider.secret(..)`.

## Pinned on purpose
<!-- 2026-11-02: Triggered by Surefire 3.6.1 rejecting suiteXmlFiles ("TestNG tests now run on the JUnit Platform"). Outcome: 3.5.6 works. Prune when: Vanguard moves to JUnit Platform runners. -->
- Surefire 3.5.x: 3.6+ rejects `testng.xml`.
<!-- 2026-11-02: Triggered by Cucumber 8.0.4 marking AbstractTestNGCucumberTests/CucumberOptions for removal; Allure ships only allure-cucumber7-jvm. Prune when: allure-cucumber8 exists and the JUnit Platform migration is done. -->
- Cucumber 7.x: 8 deprecates `cucumber-testng`.
<!-- 2026-11-02: Triggered by ~30% of ChromeDriver session starts hanging 3 min (h2c upgrade on POST /session never answered). Outcome: 12/12 fast starts with HTTP_1_1. Prune when: Selenium/chromedriver fix h2c handling. -->
- Every driver/HTTP client uses HTTP/1.1 (`ClientConfig...version("HTTP_1_1")`); keep it.

## Before reporting done
1. Run the affected module with `vanguard-reporting`; mobile: the dry run.
2. Changed a step, page or command used in `docs/`? Update the guide and `docs/STEP_CATALOGUE.md`.
3. Conventional Commits scoped to module, e.g. `feat(web): ...`.
