---
name: add-test
description: Add a new Vanguard test (web, API, mobile, load or E2E) the junior-friendly way - feature file, page/screen/client, steps, docs - and verify it. Use when asked to write, add or extend a test or a Page/Screen Object.
---

# Add a Vanguard test

1. **Find reuse first.** Grep existing step texts (`@Given|@When|@Then`) and `docs/STEP_CATALOGUE.md`; reuse matching steps instead of adding near-duplicates.
2. **Start from the template** for the domain: `templates/<web|api|mobile|load|e2e>/`. Copy it into the module paths given in its header and replace every `TODO`.
   - Web page → `vanguard-web/src/main/java/com/vanguard/web/pages/` (extends `BasePage`)
   - API client/DTO/schema → `vanguard-api/src/main/...` (client extends `BaseApiClient`; DTO is a record)
   - Fake endpoint → `vanguard-core/src/main/resources/wiremock/mappings/`
   - Screen → `vanguard-mobile/src/main/java/com/vanguard/mobile/screens/` (`MobileLocator`)
   - Simulation → `vanguard-load/src/main/java/com/vanguard/load/simulation/` (extends `ProfiledSimulation`); often `ApiBrowseSimulation` is enough
   - Steps → `<module>/src/test/java/com/vanguard/<module>/steps/`; features → `<module>/src/test/resources/features/`
3. **Feature file:** header comment (What it tests / Uses / Run it alone), type tag, `@TEST_<KEY>-<n>` when a Jira id is known, declarative steps.
4. **Run the feature before writing steps** to get Cucumber's snippets: `mvn verify -pl <module>,vanguard-reporting -am -Dcucumber.filter.tags="@<tag>"`.
5. **Implement** the steps (1-3 lines each + AssertJ) and the page/screen/client methods.
6. **Verify:**
   - the module + tag command shows `<module>  N  0  0` and `BUILD SUCCESS`;
   - mobile: the dry run (`-Pmobile ... -Dcucumber.execution.dry-run=true`) unless a device is available;
   - run it 3 times with `-Dretry.max.count=0` when it touches the UI (it must pass without retries).
7. **Docs:** new reusable step in `src/main` → add it to `docs/STEP_CATALOGUE.md`. New pattern a junior would copy → mention it in the module `README.md`.
