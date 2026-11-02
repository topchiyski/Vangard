package com.vanguard.mobile.driver;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Uploads a local .apk/.ipa/.zip to BrowserStack App Automate once per run and returns its {@code bs://} id.
 * Uses java.net.http only (multipart body built by hand).
 */
@Slf4j
public final class BrowserStackAppUploader {

    private static final URI UPLOAD_URL = URI.create("https://api-cloud.browserstack.com/app-automate/upload");
    private static final Map<Path, String> CACHE = new ConcurrentHashMap<>();

    private BrowserStackAppUploader() {
    }

    public static String upload(Path app, String username, String accessKey) {
        return CACHE.computeIfAbsent(app.toAbsolutePath(), path -> doUpload(path, username, accessKey));
    }

    private static String doUpload(Path app, String username, String accessKey) {
        String boundary = "vanguard-" + UUID.randomUUID();
        try {
            byte[] head = ("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"file\"; filename=\"" + app.getFileName() + "\"\r\n"
                    + "Content-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.UTF_8);
            byte[] tail = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);
            String auth = Base64.getEncoder().encodeToString((username + ":" + accessKey).getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder(UPLOAD_URL)
                    .timeout(Duration.ofMinutes(5))
                    .header("Authorization", "Basic " + auth)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.concat(
                            HttpRequest.BodyPublishers.ofByteArray(head),
                            HttpRequest.BodyPublishers.ofFile(app),
                            HttpRequest.BodyPublishers.ofByteArray(tail)))
                    .build();
            log.info("Uploading {} to BrowserStack...", app.getFileName());
            HttpResponse<String> response = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new IllegalStateException("BrowserStack upload failed: HTTP " + response.statusCode()
                        + " - " + response.body());
            }
            String appUrl = new ObjectMapper().readTree(response.body()).path("app_url").asText();
            log.info("Uploaded {} as {}", app.getFileName(), appUrl);
            return appUrl;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot upload " + app + " to BrowserStack", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while uploading the app", e);
        }
    }

    /** True when the configured app is already a BrowserStack id. */
    static boolean isBrowserStackId(String app) {
        return app.startsWith("bs://");
    }

    static boolean exists(String app) {
        return Files.exists(Path.of(app));
    }
}
