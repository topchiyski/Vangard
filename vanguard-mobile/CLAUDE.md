# vanguard-mobile

- Dev machines usually have no device: verify with the dry run `mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am -Dcucumber.execution.dry-run=true`.
- Never claim a mobile change works on a device unless it actually ran on one; say "dry run only".
- Locators come from the sample app source (saucelabs/my-demo-app-rn v1.3.0, `src/config/translations/en.ts`): buttons are "<label> button", inputs are "<label> input field".
- Android menu = `open menu`, iOS menu = `tab bar option menu`; cart = `cart badge` (Android) vs `tab bar option cart` (iOS).
- Screens extend `BaseScreen`; locators are `MobileLocator`; one class serves both platforms.
- `@mobileweb` steps reuse `vanguard-web` Page Objects. The runner glue must NOT include `com.vanguard.web.hooks`, which would start desktop Chrome.
- Module default is `threads=1`; tests are skipped without `-Pmobile`.
