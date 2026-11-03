package com.vanguard.core.config;

import org.aeonbits.owner.Config;

/**
 * Every setting Vanguard understands, in one place.
 *
 * <p>Values are read in this order (first match wins):
 * <ol>
 *   <li>JVM system properties, e.g. {@code mvn verify -Dheadless=false}</li>
 *   <li>{@code config/<env>.properties} on the classpath (env defaults to {@code qa})</li>
 *   <li>The {@link DefaultValue} below</li>
 * </ol>
 *
 * <p>Do not call this interface directly. Use {@link ConfigProvider#get()}.
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config/${env}.properties"
})
public interface FrameworkConfig extends Config {

    @Key("env")
    @DefaultValue("qa")
    String env();

    // ---------- URLs ----------
    @Key("ui.base.url")
    @DefaultValue("https://www.saucedemo.com")
    String uiBaseUrl();

    /**
     * Password of the SauceDemo demo users (public demo credentials; real secrets belong in env vars).
     */
    @Key("ui.password")
    @DefaultValue("secret_sauce")
    String uiPassword();

    /**
     * Empty = use the embedded WireMock server.
     */
    @Key("api.base.url")
    @DefaultValue("")
    String apiBaseUrl();

    /**
     * Empty = use the embedded WireMock server. Never point this at a system you don't own.
     */
    @Key("load.base.url")
    @DefaultValue("")
    String loadBaseUrl();

    // ---------- Execution ----------
    @Key("threads")
    @DefaultValue("4")
    int threads();

    @Key("retry.max.count")
    @DefaultValue("1")
    int retryMaxCount();

    // ---------- Web ----------
    @Key("browser.mode")
    @DefaultValue("desktop")
    String browserMode();

    @Key("browser.headless")
    @DefaultValue("true")
    boolean headless();

    @Key("emulation.device.name")
    @DefaultValue("Pixel 7")
    String emulationDeviceName();

    // ---------- Mobile ----------
    @Key("mobile.platform")
    @DefaultValue("android")
    String mobilePlatform();

    @Key("mobile.target")
    @DefaultValue("local")
    String mobileTarget();

    @Key("mobile.appium.url")
    @DefaultValue("http://127.0.0.1:4723")
    String appiumUrl();

    @Key("mobile.appium.autostart")
    @DefaultValue("true")
    boolean appiumAutostart();

    @Key("mobile.android.device")
    @DefaultValue("emulator-5554")
    String androidDevice();

    @Key("mobile.android.version")
    @DefaultValue("")
    String androidVersion();

    @Key("mobile.ios.device")
    @DefaultValue("iPhone 15")
    String iosDevice();

    @Key("mobile.ios.version")
    @DefaultValue("17.5")
    String iosVersion();

    @Key("mobile.android.app")
    @DefaultValue("apps/android/mda.apk")
    String androidApp();

    @Key("mobile.ios.app")
    @DefaultValue("apps/ios/mda.app.zip")
    String iosApp();

    @Key("browserstack.username")
    @DefaultValue("")
    String browserstackUsername();

    @Key("browserstack.access.key")
    @DefaultValue("")
    String browserstackAccessKey();

    @Key("browserstack.build.name")
    @DefaultValue("Vanguard - ${env}")
    String browserstackBuildName();

    // ---------- Load ----------
    @Key("load.profile")
    @DefaultValue("smoke")
    String loadProfile();

    // ---------- Visual ----------
    @Key("visual.enabled")
    @DefaultValue("false")
    boolean visualEnabled();

    @Key("visual.runner")
    @DefaultValue("ufg")
    String visualRunner();

    @Key("visual.batch.name")
    @DefaultValue("Vanguard - ${env}")
    String visualBatchName();

    // ---------- Xray ----------
    @Key("xray.upload.enabled")
    @DefaultValue("false")
    boolean xrayUploadEnabled();

    @Key("xray.mode")
    @DefaultValue("cloud")
    String xrayMode();

    @Key("xray.base.url")
    @DefaultValue("https://xray.cloud.getxray.app")
    String xrayBaseUrl();

    @Key("xray.client.id")
    @DefaultValue("")
    String xrayClientId();

    @Key("xray.client.secret")
    @DefaultValue("")
    String xrayClientSecret();

    @Key("xray.token")
    @DefaultValue("")
    String xrayToken();

    @Key("xray.project.key")
    @DefaultValue("QA")
    String xrayProjectKey();
}
