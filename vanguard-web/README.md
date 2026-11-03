# vanguard-web

Website tests in Chrome, as desktop (1920x1080) or phone-sized (`@mobile-emulation`, Pixel 7).
Samples run against https://www.saucedemo.com.

| Reference example | `src/test/resources/features/web_saucedemo_e2e.feature` + `pages/*Page.java` + `ShoppingSteps` |
|---|---|
| Phone-sized Chrome | `web_responsive.feature` |
| Visual checks | `web_visual.feature` (skipped unless `-Dvisual.enabled=true` and `APPLITOOLS_API_KEY`) |
| Template | `templates/web/` |
| Recipe | [Recipe 1](../docs/ONBOARDING_GUIDE.md#recipe-1-a-new-web-test) |

Run: `mvn verify -pl vanguard-web,vanguard-reporting -am` · watch it with `-Dbrowser.headless=false`

**Where do I add…?**
- a Page Object → `src/main/java/com/vanguard/web/pages/` (extend `BasePage`)
- steps → `src/test/java/com/vanguard/web/steps/` · features → `src/test/resources/features/`

Every scenario that uses the browser must be tagged `@web`; that tag starts Chrome.
Page Objects are in `src/main`, so E2E and mobile-web tests reuse them.
