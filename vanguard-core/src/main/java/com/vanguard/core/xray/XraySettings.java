package com.vanguard.core.xray;

import com.vanguard.core.config.ConfigProvider;
import com.vanguard.core.config.FrameworkConfig;

/**
 * Everything the Xray exporter needs. Built from config, or directly in unit tests.
 *
 * @param mode         {@code cloud} or {@code datacenter}
 * @param baseUrl      Xray Cloud URL (e.g. https://xray.cloud.getxray.app) or Jira Data Center URL
 * @param clientId     Cloud API key client id
 * @param clientSecret Cloud API key client secret
 * @param token        Data Center personal access token
 * @param projectKey   Jira project key, used to warn about scenarios without a matching @TEST_ tag
 */
public record XraySettings(String mode, String baseUrl, String clientId, String clientSecret,
                           String token, String projectKey) {

    /**
     * True for Xray Cloud, false for Xray Data Center ({@code xray.mode=datacenter}).
     */
    public boolean isCloud() {
        return !"datacenter".equalsIgnoreCase(mode);
    }

    /**
     * The base URL without a trailing "/".
     */
    public String normalizedBaseUrl() {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    /**
     * Reads settings from config; secrets fall back to XRAY_CLIENT_ID / XRAY_CLIENT_SECRET / XRAY_TOKEN.
     */
    public static XraySettings fromConfig() {
        FrameworkConfig c = ConfigProvider.get();
        return new XraySettings(
                c.xrayMode(),
                c.xrayBaseUrl(),
                ConfigProvider.secret(c.xrayClientId(), "XRAY_CLIENT_ID"),
                ConfigProvider.secret(c.xrayClientSecret(), "XRAY_CLIENT_SECRET"),
                ConfigProvider.secret(c.xrayToken(), "XRAY_TOKEN"),
                c.xrayProjectKey());
    }

    @Override
    public String toString() {
        // WHY: never print secrets in logs.
        return "XraySettings[mode=" + mode + ", baseUrl=" + baseUrl + ", projectKey=" + projectKey + "]";
    }
}
