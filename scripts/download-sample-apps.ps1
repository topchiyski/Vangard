# Downloads the public Sauce Labs "My Demo App" (React Native) used by vanguard-mobile.
#   apps\android\mda.apk      Android app
#   apps\ios\mda.app.zip      iOS SIMULATOR build (real devices / BrowserStack need the .ipa: see MOBILE_SETUP.md)
$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'
$version = 'v1.3.0'
$base = "https://github.com/saucelabs/my-demo-app-rn/releases/download/$version"
$root = Split-Path -Parent $PSScriptRoot

New-Item -ItemType Directory -Force "$root\apps\android", "$root\apps\ios" | Out-Null
Write-Host 'Downloading Android app...'
Invoke-WebRequest "$base/Android-MyDemoAppRN.1.3.0.build-244.apk" -OutFile "$root\apps\android\mda.apk"
Write-Host 'Downloading iOS simulator app...'
Invoke-WebRequest "$base/iOS-Simulator-MyRNDemoApp.1.3.0-162.zip" -OutFile "$root\apps\ios\mda.app.zip"
Write-Host "Done. Apps are in $root\apps"
