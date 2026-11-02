# ─────────────────────────────────────────────────────────────────────────────
# EXAMPLE (MOBILE VISUAL) - Applitools checks of native screens.
# Skipped with a warning unless -Dvisual.enabled=true and APPLITOOLS_API_KEY are set.
# ─────────────────────────────────────────────────────────────────────────────
@mobile @native @visual @example
Feature: Mobile app looks right

  @TEST_QA-531
  Scenario: Product catalog looks as approved
    Given the app is launched
    Then the "Product catalog" screen matches its visual baseline
