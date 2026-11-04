package com.vanguard.core.visual;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class VisualTestSupportTest {

    @Test
    public void acceptsNewBaselinesAndPassedResults() {
        assertThatCode(() -> VisualTestSupport.reportResult(true, false, "New", "https://example.test", "Review"))
                .doesNotThrowAnyException();
        assertThatCode(() -> VisualTestSupport.reportResult(false, true, "Passed", "https://example.test", "Review"))
                .doesNotThrowAnyException();
    }

    @Test
    public void failsWhenVisualDifferencesAreFound() {
        assertThatThrownBy(() -> VisualTestSupport.reportResult(
                false, false, "Unresolved", "https://example.test", "Review in Applitools"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Visual differences found (Unresolved)")
                .hasMessageContaining("https://example.test");
    }
}
