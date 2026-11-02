# vanguard-core

The shared engine that every module uses. **You rarely change it.** The usual exceptions:

| You want to… | Change |
|---|---|
| Share a new kind of value between steps | add a key to `src/main/java/com/vanguard/core/context/ContextKey.java` |
| Add a fake API endpoint (stub) | add a JSON file to `src/main/resources/wiremock/mappings/` (bodies in `__files/`) |
| Change environment URLs | `src/main/resources/config/<env>.properties` |
| Add a configuration setting | `src/main/java/com/vanguard/core/config/FrameworkConfig.java` |

What lives here:

| Package | Purpose |
|---|---|
| `config` | `ConfigProvider.get()`: all settings; `Endpoints`: base URLs (WireMock when empty) |
| `context` | `ScenarioContext`: per-scenario data sharing |
| `driver` | `DriverRegistry`: one browser or phone driver per scenario, created lazily |
| `hooks` | Screenshots, URL and console logs on failure; quitting drivers; starting WireMock |
| `mock` | `WireMockManager`: one local fake API server per run |
| `retry` | Re-runs failed scenarios (`retry.max.count`) |
| `report` / `xray` | Merging Cucumber results; uploading to Jira Xray |
| `steps` | Common steps for every module (`I remember "..." as "..."`) |

Run its unit tests: `mvn verify -pl vanguard-core`
