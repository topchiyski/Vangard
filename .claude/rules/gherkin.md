---
paths:
  - "**/*.feature"
---
# Feature files
- Use declarative business language, with no CSS, ids or clicks in steps. Before adding a step, reuse an existing step text (grep `@Given|@When|@Then`, or check docs/STEP_CATALOGUE.md).
- Start with the header comment: What it tests / Uses / Run it alone (the exact mvn command).
- Use one type tag (`@api|@web|@mobile|@load|@e2e`); browser use requires `@web`; Jira links are `@TEST_<KEY>-<n>`.
- One behaviour per scenario, 3-8 steps; use `Scenario Outline` for data variations.
