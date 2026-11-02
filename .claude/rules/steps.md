---
paths:
  - "**/steps/**/*.java"
---
# Step definitions
- Constructor-inject pages, screens, clients and `ScenarioContext`. No fields holding drivers, no `static` state.
- Each step: 1-3 calls to a Page/Screen Object or client, then an AssertJ assertion, with `.as("...")` when the value isn't obvious.
- In reusable steps, resolve `{placeholders}` in text parameters with `context.resolve(..)`.
- A new reusable step in `src/main/.../steps` must also be added to docs/STEP_CATALOGUE.md.
