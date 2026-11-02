package com.vanguard.reporting;

import com.vanguard.core.config.ConfigProvider;
import com.vanguard.core.config.FrameworkConfig;
import com.vanguard.core.visual.VisualSettings;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** Writes the "Environment" widget shown on the Allure report overview page. */
final class EnvironmentWriter {

    private EnvironmentWriter() {
    }

    static void write(Path target) {
        FrameworkConfig c = ConfigProvider.get();
        Map<String, String> env = new LinkedHashMap<>();
        env.put("Java", System.getProperty("java.version"));
        env.put("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        env.put("Environment", c.env());
        env.put("Browser.Mode", c.browserMode());
        env.put("Mobile.Platform", c.mobilePlatform());
        env.put("Mobile.Target", c.mobileTarget());
        env.put("Load.Profile", c.loadProfile());
        env.put("Visual.Enabled", String.valueOf(VisualSettings.isEffectivelyEnabled()));
        env.put("Xray.Upload", String.valueOf(c.xrayUploadEnabled()));
        try {
            Files.createDirectories(target.getParent());
            Files.writeString(target, env.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining(System.lineSeparator())));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write " + target, e);
        }
    }
}
