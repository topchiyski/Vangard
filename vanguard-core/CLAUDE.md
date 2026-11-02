# vanguard-core

- Changing anything here affects every module: run `mvn clean verify` (full), not just this module.
- Unit tests live in `src/test` and fail the build directly (`vanguard.ignoreTestFailures=false` in this pom).
- New config key: add to `FrameworkConfig` with `@DefaultValue`, and add it to the table in the root `README.md`.
- New WireMock stub: `src/main/resources/wiremock/mappings/*.json`. Stubs must stay stateless (scenarios run in parallel).
- `@After` order: `CoreHooks.AFTER_EVIDENCE` (10000, screenshots) > `AFTER_MODULE` (5000, module hooks) > `AFTER_QUIT` (0).
- The Xray exporter must never throw: log ERROR and return `Optional.empty()`.
<!-- 2026-11-02: Triggered by Owner leaving ${env} unexpanded when the system property is missing. Outcome: ConfigProvider sets it before create(). Prune when: config loading is replaced. -->
- `ConfigProvider` must set the Owner `env` property before `ConfigFactory.create`; keep that order.
