# vanguard-load

Load tests written in Gherkin and run by **Gatling (Java DSL, no Scala)** in a separate JVM.

> Never load-test systems your team does not own. The default target is the built-in WireMock server.

| Reference example | `src/test/resources/features/api_load.feature` + `simulation/ApiBrowseSimulation.java` |
|---|---|
| Template | `templates/load/` |
| Recipe | [Recipe 4](../docs/JUNIOR_ONBOARDING_GUIDE.md#recipe-4-a-new-load-test) |

Run:
```bash
mvn verify -pl vanguard-load,vanguard-reporting -am                      # smoke: max 5 users / 15 s per test
mvn verify -pl vanguard-load,vanguard-reporting -am -Dload.profile=full  # the numbers in the feature files
```

Results: SLA checks in the scenario, a results table plus a link to the Gatling HTML report in Allure, and full reports in `target/gatling/`.

**Where do I add…?** Simulations → `src/main/java/com/vanguard/load/simulation/` (extend `ProfiledSimulation`).
Endpoint names → `profile/LoadEndpoints.java`. Features → `src/test/resources/features/`.

Load scenarios run one at a time and are never retried.
