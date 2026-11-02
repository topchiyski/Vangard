# vanguard-load

- Test command: `mvn verify -pl vanguard-load,vanguard-reporting -am` (smoke caps of 5 users / 15 s per test; ~2 min total).
- Never add a default target other than WireMock: `load.base.url` stays empty in all env files.
- Simulations extend `ProfiledSimulation` and use `loadShape()`, `path()`, `thinkTime()` and `httpProtocol()`.
- No Scala sources. The load runner is serial and has no `RetryTransformer` in `testng.xml`; keep both.
<!-- 2026-11-02: Triggered by Gatling 3.16 no longer writing js/stats.json, and Windows stripping quotes from JSON CLI args. Prune when: the Gatling version changes (re-verify both). -->
- `LoadResult` parses the console "Global Information" block, and the profile reaches the forked JVM as a file. Re-verify both after any Gatling upgrade.
