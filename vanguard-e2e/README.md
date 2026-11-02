# vanguard-e2e

Journeys that cross layers: data from the API, actions in the web shop, then cross-checks.
This module **reuses** the clients and Page Objects of `vanguard-api` and `vanguard-web`; it never copies them.

| Reference example | `src/test/resources/features/catalog_to_checkout.feature` + `CatalogJourneySteps` |
|---|---|
| Template | `templates/e2e/` |
| Recipe | [Recipe 5](../docs/JUNIOR_ONBOARDING_GUIDE.md#recipe-5-share-data-between-steps) |

Run: `mvn verify -pl vanguard-e2e,vanguard-reporting -am`

Rules: data passes between layers only through `ScenarioContext`. Tag journeys that use the browser `@web`.
The ready-made API and web steps are available here too (see `E2eTestRunner` glue).
