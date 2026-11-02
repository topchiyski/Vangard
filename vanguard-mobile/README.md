# vanguard-mobile

Android and iOS tests with Appium: native apps (`@native`), apps with web views (`@hybrid`) and the phone's real browser (`@mobileweb`).
They run locally (emulator, simulator or device) or on BrowserStack.

**Setup first:** [docs/MOBILE_SETUP.md](../docs/MOBILE_SETUP.md). Mobile tests only run with `-Pmobile`.

| Reference example | `src/test/resources/features/native_shopping.feature` + `screens/*Screen.java` + `NativeShoppingSteps` |
|---|---|
| Hybrid | `hybrid_webview.feature` |
| Phone browser | `mobile_web_saucedemo.feature` (reuses the web Page Objects) |
| Template | `templates/mobile/` |
| Recipe | [Recipe 3](../docs/JUNIOR_ONBOARDING_GUIDE.md#recipe-3-a-new-mobile-test-android--ios) |

Run:
```bash
mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am                          # Android
mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dmobile.platform=ios    # iOS (Mac)
mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.execution.dry-run=true  # no device
```

**Where do I add…?** Screen Objects → `src/main/java/com/vanguard/mobile/screens/` (extend `BaseScreen`, locators as `MobileLocator`).
Steps → `src/test/java/com/vanguard/mobile/steps/`.

One Screen Object serves both platforms: `MobileLocator.accessibilityId("...")` when the ids match, `new MobileLocator(android, ios)` when they differ.
