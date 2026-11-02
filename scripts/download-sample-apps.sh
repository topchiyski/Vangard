#!/usr/bin/env bash
# Downloads the public Sauce Labs "My Demo App" (React Native) used by vanguard-mobile.
#   apps/android/mda.apk      Android app
#   apps/ios/mda.app.zip      iOS SIMULATOR build (real devices / BrowserStack need the .ipa: see MOBILE_SETUP.md)
set -euo pipefail
VERSION="v1.3.0"
BASE="https://github.com/saucelabs/my-demo-app-rn/releases/download/${VERSION}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

mkdir -p "$ROOT/apps/android" "$ROOT/apps/ios"
echo "Downloading Android app..."
curl -fL --progress-bar -o "$ROOT/apps/android/mda.apk" "$BASE/Android-MyDemoAppRN.1.3.0.build-244.apk"
echo "Downloading iOS simulator app..."
curl -fL --progress-bar -o "$ROOT/apps/ios/mda.app.zip" "$BASE/iOS-Simulator-MyRNDemoApp.1.3.0-162.zip"
echo "Done. Apps are in $ROOT/apps"
