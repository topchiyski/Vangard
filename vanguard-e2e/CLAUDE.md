# vanguard-e2e

- Test command: `mvn verify -pl vanguard-e2e,vanguard-reporting -am -Dcucumber.filter.tags="@tag"`.
- Reuse clients and pages from api/web via constructor injection. Instead of copying a class, move it to the owning module's `src/main`.
- `E2eTestRunner` glue lists the hooks/steps packages explicitly. Add `com.vanguard.mobile.hooks` only for phone journeys (plus the mobile dependency under `-Pmobile`).
- Pass data across layers only through `ScenarioContext` keys.
