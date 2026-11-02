# Cheat Sheet

## Run

| I want to… | Command |
|---|---|
| Run everything (mobile skipped) | `mvn clean verify` |
| Run one module | `mvn verify -pl vanguard-api,vanguard-reporting -am` (swap in `vanguard-web`, `vanguard-load`, `vanguard-e2e`) |
| Run one tag | add `-Dcucumber.filter.tags="@smoke"` |
| Combine tags | `-Dcucumber.filter.tags="@web and not @visual"` · `"@api or @e2e"` |
| Watch the browser | add `-Dbrowser.headless=false` |
| Phone-sized Chrome | add `-Dbrowser.mode=mobile` |
| Run mobile | `mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am` |
| iOS instead of Android | add `-Dmobile.platform=ios` |
| Mobile on BrowserStack | add `-Dmobile.target=browserstack` |
| Check steps exist, without running | add `-Dcucumber.execution.dry-run=true` |
| Full load (not the smoke cap) | add `-Dload.profile=full` |
| Fewer / more parallel scenarios | add `-Dthreads=1` / `-Dthreads=8` |
| No retries | add `-Dretry.max.count=0` |
| Another environment | add `-Denv=staging` |
| Visual checks for real | set `APPLITOOLS_API_KEY`, add `-Dvisual.enabled=true` |
| Upload to Jira Xray | set `XRAY_CLIENT_ID`/`XRAY_CLIENT_SECRET`, add `-Dxray.upload.enabled=true` |
| Open the report | `mvn -N allure:serve` |
| Download the mobile sample apps | `.\scripts\download-sample-apps.ps1` (Windows) · `./scripts/download-sample-apps.sh` (Mac/Linux) |

## Tags

| Tag | Meaning |
|---|---|
| `@api` `@web` `@mobile` `@load` `@e2e` | Test type (one per feature) |
| `@desktop` / `@mobile-emulation` | Web: desktop Chrome / phone-sized Chrome |
| `@native` `@hybrid` `@mobileweb` | Mobile: installed app / app with web views / phone browser |
| `@android` `@ios` | Mobile: run on this platform only |
| `@visual` | Applitools visual checks |
| `@smoke` | Most important checks; run on every change |
| `@example` | Reference examples to copy from |
| `@wip` | Work in progress; never run by default |
| `@TEST_QA-123` | Linked to Jira Xray test QA-123 |

## Where things go

| I am adding… | Put it in |
|---|---|
| A feature file | `<module>/src/test/resources/features/` |
| Step definitions | `<module>/src/test/java/com/vanguard/<module>/steps/` |
| A Page Object | `vanguard-web/src/main/java/com/vanguard/web/pages/` |
| A Screen Object | `vanguard-mobile/src/main/java/com/vanguard/mobile/screens/` |
| An API client / DTO | `vanguard-api/src/main/java/com/vanguard/api/client/` / `.../dto/` |
| A JSON schema | `vanguard-api/src/main/resources/schemas/` |
| A fake API answer (stub) | `vanguard-core/src/main/resources/wiremock/mappings/` |
| A load simulation | `vanguard-load/src/main/java/com/vanguard/load/simulation/` |
| A ScenarioContext key | `vanguard-core/src/main/java/com/vanguard/core/context/ContextKey.java` |
| An environment setting | `vanguard-core/src/main/resources/config/<env>.properties` |
