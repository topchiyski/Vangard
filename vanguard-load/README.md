# vanguard-load

Load tests written in Gherkin and run by **Gatling (Java DSL, no Scala)** in a separate JVM.

> Never load-test systems your team does not own. The default target is the built-in WireMock server.

| Reference example | `src/test/resources/features/api_load.feature` + `simulation/ApiBrowseSimulation.java` |
|---|---|
| Template | `templates/load/` |
| Recipe | [Recipe 4](../docs/JUNIOR_ONBOARDING_GUIDE.md#recipe-4-a-new-load-test) |

Run:
```bash
mvn verify -pl vanguard-load,vanguard-reporting -am                      # smoke: max 5 users / 8 s per test
mvn verify -pl vanguard-load,vanguard-reporting -am -Dload.profile=full  # the numbers in the feature files
```

Results: SLA checks in the scenario, a results table plus a link to the Gatling HTML report in Allure, and full reports in `target/gatling/`.

## Which files do I edit?

| You want to… | Edit | Java? |
|---|---|---|
| Load-test a GET endpoint | a feature in `src/test/resources/features/` with `When I run the load test` | no |
| Give an endpoint a short name (`"products"`) | `profile/LoadEndpoints.java` (one line) | one line |
| Test a multi-step journey or a POST | a new class in `simulation/` (copy `templates/load/ExampleSimulation.java`) + `When I run the "<Name>Simulation" load test` | yes |

You never need to edit `LoadSteps`, `LoadProfile`, `GatlingRunner` or `LoadResult`.

## How a load test runs

1. `Given a load profile ...`: `LoadSteps` reads the table into a `LoadProfile` and checks the endpoint name. Unless `-Dload.profile=full` is set, it caps the profile to the smoke size.
2. `When I run ... load test`: `GatlingRunner` writes the profile to `target/gatling/<name>-<time>/profile.json` and starts Gatling in a **separate JVM**, so load generation never slows the test runner.
3. Inside that JVM, the simulation (a `ProfiledSimulation`) reads `profile.json` and sends the load.
4. Gatling prints a "Global Information" summary and writes its HTML report. `LoadResult` reads the numbers from the summary.
5. `Then ...` steps compare those numbers with your limits. The numbers and a report link are attached to Allure.

Load scenarios run one at a time and are never retried.
