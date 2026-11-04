package com.vanguard.core.config;

import org.aeonbits.owner.ConfigFactory;

/**
 * The single entry point for configuration.
 *
 * <pre>{@code
 * String url = ConfigProvider.get().uiBaseUrl();
 * }</pre>
 */
public final class ConfigProvider {

    private static volatile FrameworkConfig instance;

    private ConfigProvider() {
    }

    /**
     * Returns the shared, thread-safe configuration.
     */
    public static FrameworkConfig get() {
        FrameworkConfig local = instance;
        if (local == null) {
            synchronized (ConfigProvider.class) {
                local = instance;
                if (local == null) {
                    // WHY: Owner expands ${env} in @Sources from this global property.
                    // Without a value the env file would silently not load.
                    ConfigFactory.setProperty("env", System.getProperty("env", "qa"));
                    local = ConfigFactory.create(FrameworkConfig.class, System.getProperties());
                    instance = local;
                }
            }
        }
        return local;
    }

    /**
     * Forces a reload on next {@link #get()}. Intended for unit tests only.
     */
    public static synchronized void reset() {
        instance = null;
    }

    /**
     * Returns {@code configValue} when it is set, otherwise the environment variable {@code envName}.
     * Used for secrets so they never have to live in properties files.
     */
    public static String secret(String configValue, String envName) {
        if (configValue != null && !configValue.isBlank()) {
            return configValue;
        }
        String fromEnv = System.getenv(envName);
        return fromEnv == null ? "" : fromEnv;
    }
}
