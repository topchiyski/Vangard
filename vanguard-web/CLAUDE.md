# vanguard-web

- Test command: `mvn verify -pl vanguard-web,vanguard-reporting -am -Dcucumber.filter.tags="@tag"` (~30 s; hits saucedemo.com, so it needs internet).
- Locators: prefer `id` / `[data-test=...]`; select products by visible name (see `InventoryPage.productCard`).
- New `BasePage` helpers must wrap waits and throw an `AssertionError` that names the page, locator and URL.
- `@web` triggers `WebHooks.registerChrome`; `@mobile-emulation` / `@desktop` override `-Dbrowser.mode`.
<!-- 2026-11-02: Triggered by Chrome's password-leak dialog blocking clicks after SauceDemo login. Prune when: no longer reproducible with the prefs removed. -->
- Keep the password-manager prefs in `ChromeDriverFactory.baseOptions`.
- Visual: use `WebEyesManager.check`. Never call Eyes directly from steps; it must skip when there is no API key.
