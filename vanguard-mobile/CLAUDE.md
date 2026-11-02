# vanguard-mobile

- Verify without a device: `mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.execution.dry-run=true`.
- Report mobile changes as "dry run only" unless they actually ran on a device, emulator or BrowserStack.
<!-- 2026-11-02: Triggered by no device/Appium on the dev machine. Outcome: ids taken from app source, dry run green. Prune when: the locators have been confirmed on a real Android + iOS run. -->
- Locators come from the sample app source (saucelabs/my-demo-app-rn v1.3.0, `src/config/translations/en.ts`): buttons are "<label> button", inputs are "<label> input field". Android menu is `open menu`, iOS menu is `tab bar option menu`.
- One Screen Object serves both platforms: `MobileLocator.accessibilityId(..)`, or `new MobileLocator(android, ios)` when they differ.
<!-- 2026-11-02: Triggered by design: mobile-web steps reuse web Page Objects, and WebHooks registers desktop Chrome for @web. Outcome: Appium drives the phone browser. Prune when: WebHooks no longer registers drivers. -->
- Keep `MobileTestRunner` glue = `com.vanguard.core` + `com.vanguard.mobile`. Web Page Objects are reached through Java imports, so the glue doesn't need web packages.
- Module default is `threads=1`; tests are skipped without `-Pmobile`.
