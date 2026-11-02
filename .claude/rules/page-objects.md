---
paths:
  - "vanguard-web/src/main/java/**/pages/**/*.java"
  - "vanguard-mobile/src/main/java/**/screens/**/*.java"
---
# Page and Screen Objects
- Locators are `private static final` at the top. Public methods are user actions that return plain data (`String`, `BigDecimal`, `List<String>`) rather than a `WebElement`.
- Use only `BasePage`/`BaseScreen` helpers. If one is missing, add it to the base class with a waiting implementation and an error message that explains the fix.
- The class Javadoc says which page or screen it models.
