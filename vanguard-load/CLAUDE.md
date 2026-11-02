# vanguard-load

- Test command: `mvn verify -pl vanguard-load,vanguard-reporting -am` (smoke caps of 5 users / 15 s per test; ~2 min).
- Default target is WireMock: keep `load.base.url` empty in every env file. Real targets are passed only via `-Dload.base.url` for systems the team owns.
- Simulations extend `ProfiledSimulation` and use `loadShape()`, `path()`, `thinkTime()` and `httpProtocol()`. Java DSL only.
<!-- 2026-11-02: Triggered by user requirement: retries hide load problems; parallel load runs skew results. Prune when: never (design). -->
- Keep the load runner serial, with no `RetryTransformer` in its `testng.xml`.
<!-- 2026-11-02: Triggered by Gatling 3.16 no longer writing js/stats.json, and Windows stripping quotes from JSON CLI args. Outcome: console-summary parsing + profile file, 5/5 green. Prune when: the Gatling version changes (re-verify both). -->
- `LoadResult` parses the console "Global Information" block, and the profile reaches the forked JVM as a file. Re-verify both after any Gatling upgrade.
