package com.vanguard.core.retry;

import com.vanguard.core.config.ConfigProvider;
import lombok.extern.slf4j.Slf4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Re-runs a failed scenario up to {@code retry.max.count} times (default 1).
 * Earlier attempts are reported as SKIPPED; the scenario passes if its last attempt passes.
 */
@Slf4j
public class RetryAnalyzer implements IRetryAnalyzer {

    // WHY: with a parallel data provider TestNG may share one analyzer across scenarios,
    // so attempts are counted per scenario (= per parameter set), not per analyzer instance.
    private final Map<List<Object>, AtomicInteger> attempts = new ConcurrentHashMap<>();
    private final int maxRetries;

    /**
     * Uses {@code retry.max.count} from the configuration.
     */
    public RetryAnalyzer() {
        this(ConfigProvider.get().retryMaxCount());
    }

    RetryAnalyzer(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean retry(ITestResult result) {
        if (isNeverFixedByRetry(result.getThrowable())) {
            return false;
        }
        List<Object> key = Arrays.asList(result.getParameters());
        int attempt = attempts.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet();
        if (attempt <= maxRetries) {
            log.warn("Retrying {} (attempt {}/{})", describe(result), attempt, maxRetries);
            return true;
        }
        return false;
    }

    /**
     * Undefined or ambiguous steps fail the same way every time: retrying only wastes time.
     */
    private static boolean isNeverFixedByRetry(Throwable failure) {
        String type = failure == null ? "" : failure.getClass().getSimpleName();
        return type.equals("UndefinedStepException") || type.startsWith("AmbiguousStepDefinitions");
    }

    private static String describe(ITestResult result) {
        Object[] params = result.getParameters();
        return params.length > 0 ? String.valueOf(params[0]) : result.getName();
    }
}
