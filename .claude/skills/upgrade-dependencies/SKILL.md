---
name: upgrade-dependencies
description: Upgrade Vanguard's Maven dependency and plugin versions safely, re-verifying the known compatibility traps (Surefire/testng.xml, Cucumber/TestNG, HTTP/1.1 driver clients, Gatling output parsing, Appium/Eyes APIs). Use when asked to update versions or when a version bump breaks the build.
---

# Upgrade dependencies

All versions live in the parent `pom.xml` `<properties>`. Change them there only.

1. **Find the latest stable versions** from Maven Central metadata (exclude alpha/beta/RC/M):
   `curl -s https://repo1.maven.org/maven2/<group/path>/<artifact>/maven-metadata.xml`
2. **Bump one family at a time** (Cucumber, Selenium/Appium, Gatling, Allure, RestAssured/Jackson, WireMock, plugins) and run `mvn clean verify` after each.
3. **Re-verify each known trap** (each is pinned or worked around in code with a `WHY:` comment):

   | Area | Check | Where |
   |---|---|---|
   | Surefire | Still accepts `suiteXmlFiles`? 3.6+ did not | parent `pom.xml` |
   | Cucumber | `cucumber-testng` not deprecated for removal; Allure plugin exists for that major | parent `pom.xml`, `ParallelCucumberRunner` |
   | Selenium / Appium | Drivers still start quickly with `ClientConfig/AppiumClientConfig.version("HTTP_1_1")` | `ChromeDriverFactory`, `AppiumDriverFactory` |
   | Gatling | CLI flags `--simulation/--results-folder`; console "Global Information" format; `--add-opens` still needed | `GatlingRunner`, `LoadResult` |
   | Applitools | `Eyes.close(false)`, `TestResults.isNew()`, `DeviceName` constants still exist | `WebEyesManager`, `MobileEyesManager` |
   | Selenium Manager | `chromedriver` matches installed Chrome | web run |

   Inspect a changed API with `javap -cp <jar> <class>` (JDK `bin/javap`) instead of guessing.
4. **Mobile:** run the dry run; it proves compilation and glue, not device behaviour.
5. If a latest version must be held back: pin the newest working one, with a pom comment saying why, and add or refresh the stamped entry under "Pinned on purpose" in the root `CLAUDE.md`.
6. Commit as `build(deps): ...` listing every version change and what was verified.
