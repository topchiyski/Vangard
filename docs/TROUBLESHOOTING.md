# Troubleshooting

Search this page (Ctrl+F) for the first words of your error message.

---

### `The step '...' is undefined. You can implement this step using the snippet(s) below`

**Cause:** no Java method matches the Gherkin line. Usually a typo, or the step class is in a package the runner doesn't scan.
**Fix:**
1. Compare your Gherkin text with the `@When("...")` text **character by character**: quotes, plurals, spaces.
2. Check the step class is in `com.vanguard.<module>.steps` (`src/test/java`).
3. Paste the snippet Cucumber printed into your step class and fill it in.

### `Step ... is defined with 2 step definitions` / `AmbiguousStepDefinitionsException`

**Cause:** two methods match the same line.
**Fix:** search the project for the step text, then delete or rename one of the two.

### `LoginPage: element By.id: user-name not visible after 10s on https://... - check the locator`

**Cause:** the element did not appear: wrong locator, wrong page, or the page is slow or broken.
**Fix:**
1. Rerun with `-Dbrowser.headless=false` and watch what happens.
2. Open the screenshot in the Allure report: is it the page you expected?
3. Check the locator in Chrome DevTools: press `Ctrl+F` in the Elements tab and paste the CSS or XPath.

### `element click intercepted: ... Other element would receive the click`

**Cause:** something covers the element (a menu, popup or cookie banner), or an animation is still running.
**Fix:** close or wait for the thing on top first, in the Page Object. For example, `InventoryPage.menuEntries()` closes the menu and calls `waitUntilHidden(...)`.

### `StaleElementReferenceException`

**Cause:** the page re-rendered after the element was found.
**Fix:** don't keep `WebElement`s in variables. Use the `BasePage` helpers (`click`, `getText`): they find the element fresh every time.

### `SessionNotCreatedException` / `This version of ChromeDriver only supports Chrome version …`

**Cause:** Chrome and chromedriver don't match, or Chrome failed to start.
**Fix:** update Chrome. Selenium Manager downloads the matching driver automatically.
Behind a company proxy, set `HTTPS_PROXY`, or ask for `~/.cache/selenium` to be pre-filled.

### `No driver factory registered for this scenario. Tag the scenario with @web or @mobile`

**Cause:** a step uses a page or screen, but the scenario has no `@web` / `@mobile` tag, so no browser was started.
**Fix:** add `@web` (or `@mobile`) to the feature or scenario.

### `No value for ContextKey.X in ScenarioContext. Available keys: [...]`

**Cause:** a step reads a value that no earlier step stored.
**Fix:** make sure an earlier step calls `context.set(ContextKey.X, ...)`. Remember that each scenario starts empty. See [Recipe 5](JUNIOR_ONBOARDING_GUIDE.md#recipe-5-share-data-between-steps).

### `Nothing remembered as "userId"`

**Cause:** `{userId}` is used before `I save the response field ... as "userId"` (or `I remember ... as ...`).
**Fix:** add the save step earlier in the scenario, and check that the spelling matches.

### API test: status `404` with body `No response could be served as there are no stubs mapped…` (WireMock)

**Cause:** the fake server has no stub for that method and URL.
**Fix:** add or fix a JSON file in `vanguard-core/src/main/resources/wiremock/mappings/`. Check `method`, `urlPath` / `urlPathPattern`, and that the JSON is valid.
The WireMock error body lists the closest stub, which shows you what differs.

### `JSON schema validation failed` / `expected type: Integer, found: String`

**Cause:** the response doesn't match the schema. Either the API changed (a real bug?) or the schema is wrong.
**Fix:** compare the response (in Allure, **Response** tab) with `schemas/<name>.json`. Change the schema only if the new shape is correct.

### Load: `Then the 95th percentile response time is below 500 ms` fails

**Cause:** the system was slower than the SLA.
**Fix:**
1. Open the Gatling report linked in Allure and look at *Response time percentiles over time*.
2. Was it slow from the start (the system is too slow) or did it get slower over time (overload)?
3. Discuss the SLA with your team before changing the number.

### Load: `No simulation class com.vanguard.load.simulation.X`

**Fix:** the name in `When I run the "X" load test` must match a class in `vanguard-load/src/main/java/com/vanguard/load/simulation/`. Check the spelling.

### Load: `Unknown load endpoint 'x'. Known: [...]`

**Fix:** use a listed name, a path that starts with `/`, or add the name to `LoadEndpoints.java`.

### Mobile: `Could not start Appium automatically. Is Node.js installed and 'appium' on your PATH...`

**Fix:** install Node.js, run `npm i -g appium`, then `appium driver install uiautomator2` (Android) and/or `appium driver install xcuitest` (iOS). See [Mobile Setup](MOBILE_SETUP.md#appium).

### Mobile: `App file not found: .../apps/android/mda.apk`

**Fix:** run `.\scripts\download-sample-apps.ps1` (Windows) or `./scripts/download-sample-apps.sh` (Mac/Linux), or point `-Dmobile.android.app=` at your app.

### Mobile: `Could not find a connected Android device` / `emulator-5554 not found`

**Fix:** start the emulator in Android Studio (Device Manager → ▶), check that `adb devices` lists it, and pass its name with `-Dmobile.android.device=...`.

### Mobile: `element ... not visible after 15s on Android - check the locator with Appium Inspector`

**Fix:** open the screen in Appium Inspector and compare the accessibility id. iOS and Android may differ, in which case use `new MobileLocator(android, ios)`.

### Mobile hybrid: timeout while switching to `WEBVIEW`

**Cause:** Android only exposes web views that the app marks as debuggable.
**Fix:** see [Mobile Setup → Hybrid apps](MOBILE_SETUP.md#hybrid-apps).

### `Visual check skipped: ...`

This is not an error. Visual checks only run with `-Dvisual.enabled=true` **and** the `APPLITOOLS_API_KEY` environment variable set.

### `Xray upload failed ... HTTP 401`

**Fix:** check `XRAY_CLIENT_ID` / `XRAY_CLIENT_SECRET` (Cloud) or `XRAY_TOKEN` (Data Center), and that `xray.mode` matches your Jira.
A failed upload never fails the test run.

### The build says `BUILD SUCCESS` but a module shows failures in its own output

You ran a module **without** `vanguard-reporting`. Vanguard modules keep going after failures, and `vanguard-reporting` decides the final verdict.
Always include it: `mvn verify -pl <module>,vanguard-reporting -am`.

### A test passes only on the retry

That is a **flaky** test. The retry hides the problem; it doesn't fix it. Look for `Thread.sleep`, missing waits, `static` fields, or tests that depend on each other's data.
