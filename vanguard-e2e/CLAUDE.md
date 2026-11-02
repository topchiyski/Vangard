# vanguard-e2e

- Test command: `mvn verify -pl vanguard-e2e,vanguard-reporting -am -Dcucumber.filter.tags="@tag"`.
<!-- 2026-11-02: Triggered by design: E2E proves the modules' own objects work together. Prune when: never (design). -->
- Reuse clients and pages from api/web via constructor injection. A class missing from `src/main` moves to its owning module instead of being copied here.
- `E2eTestRunner` glue lists hooks and steps packages explicitly; journeys with a phone add `com.vanguard.mobile.hooks` (plus the mobile dependency under `-Pmobile`).
- Data crosses layers only through `ScenarioContext` keys.
