# vanguard-web

- Test command: `mvn verify -pl vanguard-web,vanguard-reporting -am -Dcucumber.filter.tags="@tag"` (~30 s; hits saucedemo.com, so it needs internet).
- Locators: `id` or `[data-test=...]`; products are selected by visible name (`InventoryPage.productCard`).
- New `BasePage` helpers wrap a wait and throw an `AssertionError` that names the page, locator and URL.
<!-- 2026-11-02: Triggered by Chrome's password-leak dialog blocking clicks after SauceDemo login. Prune when: no longer reproducible with the prefs removed. -->
- Keep the password-manager prefs in `ChromeDriverFactory.baseOptions`.
<!-- 2026-11-02: Triggered by design: visual checks must pass without an API key for local runs. Prune when: never (design). -->
- Visual checks go through `WebEyesManager.check` (it handles the skip without a key); steps call it instead of Eyes.
<!-- 2026-11-02: Triggered by the burger menu staying shut when clicked right after login in parallel runs. Outcome: 3x8 threads green without retries. Prune when: saucedemo changes its menu. -->
- Pages with animations or late handlers: confirm the effect with `appearsWithin(..)` and act again, instead of relying on retries.
