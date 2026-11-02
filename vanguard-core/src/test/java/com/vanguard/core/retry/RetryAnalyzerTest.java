package com.vanguard.core.retry;

import org.testng.ITestResult;
import org.testng.annotations.Test;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

public class RetryAnalyzerTest {

    @Test
    public void retriesEachScenarioUpToTheLimit() {
        RetryAnalyzer analyzer = new RetryAnalyzer(1);
        ITestResult scenarioA = resultWith("scenario A");
        ITestResult scenarioB = resultWith("scenario B");

        assertThat(analyzer.retry(scenarioA)).isTrue();
        assertThat(analyzer.retry(scenarioA)).isFalse();
        assertThat(analyzer.retry(scenarioB)).as("other scenarios keep their own budget").isTrue();
    }

    private static ITestResult resultWith(Object parameter) {
        Object[] params = {parameter};
        return (ITestResult) Proxy.newProxyInstance(ITestResult.class.getClassLoader(),
                new Class<?>[]{ITestResult.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getParameters" -> params;
                    case "getName" -> "runScenario";
                    default -> null;
                });
    }
}
