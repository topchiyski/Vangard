# Mobile Setup

Mobile tests need three things: **a phone** (an emulator, simulator, real device or BrowserStack), **Appium**, and **the app**.
Allow about 30 minutes for the first setup.

| Platform | Where it can run |
|---|---|
| Android | Windows, Mac, Linux (emulator or USB device) · BrowserStack |
| iOS | **Mac only** (simulator or device) · BrowserStack from any computer |

## 1. The sample app

Vanguard's examples use Sauce Labs' public **My Demo App** (React Native v1.3.0). It has the same accessibility ids on Android and iOS.

```bash
# Windows
.\scripts\download-sample-apps.ps1
# Mac / Linux
./scripts/download-sample-apps.sh
```

What you should see: `apps/android/mda.apk` and `apps/ios/mda.app.zip` (an iOS **simulator** build).
For real iPhones or BrowserStack iOS, download `iOS-Real-Device-MyRNDemoApp.1.3.0-162.ipa` from the
[release page](https://github.com/saucelabs/my-demo-app-rn/releases/tag/v1.3.0) and pass `-Dmobile.ios.app=path/to/app.ipa`.

## Appium

1. Install **Node.js 20+** from https://nodejs.org.
2. Install Appium and the drivers:

   ```bash
   npm install -g appium
   appium driver install uiautomator2   # Android
   appium driver install xcuitest       # iOS (Mac only)
   ```

3. Check your machine (optional but helpful):

   ```bash
   npm install -g appium-doctor
   appium-doctor --android     # or --ios
   ```

You don't need to start Appium yourself: Vanguard starts it when no server answers at `http://127.0.0.1:4723`.
To use a server you started manually, run `appium` in a terminal, or pass `-Dmobile.appium.autostart=false`.

## 2. Android emulator (Windows / Mac / Linux)

1. Install **Android Studio** → **More Actions → Virtual Device Manager → Create device** → *Pixel 7*, a recent system image → **Finish**.
2. Start it with ▶.
3. Set `ANDROID_HOME` (Android Studio → Settings → Android SDK shows the path) and add `platform-tools` to your PATH.
4. Check:

   ```bash
   adb devices
   ```

   What you should see: `emulator-5554   device`.
5. Run the mobile examples:

   ```bash
   mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am
   ```

A real Android phone works too: enable **Developer options → USB debugging**, plug it in, then use the name `adb devices` shows: `-Dmobile.android.device=<id>`.

## 3. iOS simulator (Mac only)

1. Install **Xcode** from the App Store, open it once, and accept the license.
2. List the simulators: `xcrun simctl list devices available`.
3. Run with a simulator from that list:

   ```bash
   mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am \
     -Dmobile.platform=ios "-Dmobile.ios.device=iPhone 15" -Dmobile.ios.version=17.5
   ```

The first iOS run takes a few minutes while Appium builds its helper app (WebDriverAgent).

## Appium Inspector

Appium Inspector shows the app's screen and every element's locators. Use it to find accessibility ids for Screen Objects.

1. Download it from https://github.com/appium/appium-inspector/releases.
2. Start Appium (`appium` in a terminal) and your emulator.
3. In Inspector, set **Remote Port** to `4723` and these **Desired Capabilities** (Android example):

   ```json
   {
     "platformName": "Android",
     "appium:automationName": "UiAutomator2",
     "appium:deviceName": "emulator-5554",
     "appium:app": "C:/code/vanguard/apps/android/mda.apk"
   }
   ```

4. Click **Start Session**, click an element on the screenshot, and read **accessibility id** in the panel on the right.

## BrowserStack

No local phone needed: tests run on real devices in the cloud.

1. Get your username and access key: BrowserStack → **Account → Settings**.
2. Set them as environment variables (never in files):

   ```bash
   # Windows PowerShell
   $env:BROWSERSTACK_USERNAME="you"; $env:BROWSERSTACK_ACCESS_KEY="key"
   # Mac/Linux
   export BROWSERSTACK_USERNAME=you BROWSERSTACK_ACCESS_KEY=key
   ```

3. Run. Vanguard uploads the app once per run:

   ```bash
   mvn verify -Pmobile -pl vanguard-mobile,vanguard-reporting -am \
     -Dmobile.target=browserstack "-Dmobile.android.device=Google Pixel 7" -Dmobile.android.version=13.0
   ```

Device names must match BrowserStack's device list. Each scenario appears in the BrowserStack dashboard as passed or failed, with a video.

## Hybrid apps

`@hybrid` scenarios switch from the native app into a web view (`When I switch to the web view`).

- **Android:** the app must make its web views debuggable (`WebView.setWebContentsDebuggingEnabled(true)`); release builds often don't. If the switch times out, ask the developers for a debug build. Appium also needs a chromedriver that matches the web view's Chrome version; Appium can download it if started with `appium --allow-insecure chromedriver_autodownload`.
- **iOS:** simulators allow web view inspection. On real devices the app must be a development build.

## Running mobile tests in CI

Mobile CI is **off by default**. The mobile jobs only run when the repository variable `MOBILE_ENABLED` is `true`.
To turn it on, open GitHub > Settings > Secrets and variables > Actions > Variables and add `MOBILE_ENABLED` = `true`.

Once enabled, the GitHub workflow runs Android on an Ubuntu emulator and iOS on a macOS runner, nightly or on demand.
For an on-demand run, add `mobile-android` and/or `mobile-ios` to the `suites` input.
Choose BrowserStack instead with the `mobile_target` input.
