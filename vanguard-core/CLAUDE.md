# vanguard-core

- Changes here affect every module: finish with `mvn clean verify`, not just `mvn verify -pl vanguard-core`.
- Unit tests in `src/test` fail the build directly (`vanguard.ignoreTestFailures=false` in this pom).
- New config key: add it to `FrameworkConfig` with `@DefaultValue`, plus a row in the root `README.md` table.
<!-- 2026-11-02: Triggered by design: scenarios run in parallel against one WireMock. Prune when: never (design). -->
- WireMock stubs (`src/main/resources/wiremock/mappings/*.json`) stay stateless: use response templating, not WireMock scenarios.
<!-- 2026-11-02: Triggered by requirement: a failed upload must not hide test results. Prune when: never (design). -->
- `XrayResultsExporter` logs ERROR and returns `Optional.empty()` on any failure; keep it non-throwing.
<!-- 2026-11-02: Triggered by Owner leaving ${env} unexpanded when the system property is missing. Outcome: ConfigProvider sets it before create(). Prune when: config loading is replaced. -->
- `ConfigProvider` sets the Owner `env` property before `ConfigFactory.create`; keep that order.
