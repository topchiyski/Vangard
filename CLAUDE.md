<!-- Maintenance: every rule needs an HTML-comment stamp "YYYY-MM-DD: Triggered by … Outcome: … Prune when: …".
     Keep this file < 60 lines. Module rules live in vanguard-*/CLAUDE.md, file-type rules in .claude/rules/,
     multi-step workflows in .claude/skills/, hard safety blocks in .claude/settings.json. -->
# Vanguard
Java 21 Maven multi-module BDD test platform (Cucumber 7 + TestNG). Users are junior QA engineers: copyable, readable patterns beat clever abstractions.

## Invariants
<!-- 2026-11-02: Source: user requirement "very easy for juniors" + parallel scenarios. Outcome: build stays green at threads=8, retries off. Prune when: never (architecture). -->
- IMPORTANT: Get pages, screens, clients and `ScenarioContext` by constructor injection (PicoContainer). Instead of `new` or `static` state, add a constructor parameter.
- Share data only via `ScenarioContext`: `ContextKey` (new keys get a comment) or `remember`/`resolve` for Gherkin `{placeholders}`.
- UI access only through `BasePage` / `BaseScreen` helpers. Instead of `driver.findElement` or `Thread.sleep` in steps, add a waiting helper there.
- Framework errors say how to fix the problem (pattern: `ScenarioContext.get`).

## Commands (verified)
- Iterate on one module + tag: `mvn verify -pl vanguard-api,vanguard-reporting -am -Dcucumber.filter.tags="@tag"`. Always include `vanguard-reporting`: it decides the verdict.
- Compile check after edits: `mvn -q -DskipTests install -pl <module> -am`.
- Core unit tests: `mvn verify -pl vanguard-core`.
- Mobile without a device: `mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.execution.dry-run=true`.
- Full run `mvn clean verify` (~3 min) only before reporting done. During iteration, use the module + tag command instead.

## Module map
- `vanguard-core`: config, context, drivers, hooks, WireMock stubs, retry, Xray. Every module depends on it.
- `vanguard-api|web|mobile|load`: reusable code in `src/main`, features/steps/runner in `src/test`. `vanguard-e2e` reuses api + web.
- `vanguard-reporting` runs last. `templates/` is compiled and never run. Each module has its own `CLAUDE.md`.

## Conventions
- Tags: browser `@web`; phone `@mobile` + `@native|@hybrid|@mobileweb`; `@load`; Jira `@TEST_<KEY>-<n>`.
- Money as `BigDecimal`; secrets from env vars via `ConfigProvider.secret(..)`.
- Changed a step, page or command used in `docs/`? Update the guide and `docs/STEP_CATALOGUE.md` in the same change.

## Pinned on purpose
<!-- 2026-11-02: Triggered by Surefire 3.6.1 rejecting suiteXmlFiles ("TestNG tests now run on the JUnit Platform"). Outcome: 3.5.6 works. Prune when: Vanguard moves to JUnit Platform runners. -->
- Surefire 3.5.x: 3.6+ rejects `testng.xml`.
<!-- 2026-11-02: Triggered by Cucumber 8.0.4 marking AbstractTestNGCucumberTests/CucumberOptions for removal; Allure ships only allure-cucumber7-jvm. Prune when: allure-cucumber8 exists and the JUnit Platform migration is done. -->
- Cucumber 7.x: 8 deprecates `cucumber-testng`.
<!-- 2026-11-02: Triggered by ~30% of ChromeDriver session starts hanging 3 min (h2c upgrade on POST /session never answered). Outcome: 12/12 fast starts with HTTP_1_1. Prune when: Selenium/chromedriver fix h2c handling. -->
- Every driver/HTTP client uses HTTP/1.1 (`ClientConfig...version("HTTP_1_1")`); keep it on new clients.
- Upgrading dependencies: use the `upgrade-dependencies` skill.

## Workflow
1. Changes touching > 2 files: explore the affected modules, state a short plan with the tests to run, then code.
2. Verify: compile check, then the affected module + tag; finish with `mvn clean verify`.
3. Conventional Commits scoped to module, e.g. `feat(web): ...`. New test or page: use the `add-test` skill.
