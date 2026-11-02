# vanguard-api

API tests with RestAssured. By default they run against the built-in WireMock fake server, so they work offline.

| Reference example | `src/test/resources/features/user_crud_and_schema.feature` + `UserClient` + `UserSteps` |
|---|---|
| No-code example | `src/test/resources/features/api_common_steps.feature` |
| Template | `templates/api/` |
| Recipe | [Recipe 2](../docs/JUNIOR_ONBOARDING_GUIDE.md#recipe-2-a-new-api-test) |

Run only this module: `mvn verify -pl vanguard-api,vanguard-reporting -am`

**Where do I add…?**
- an API client → `src/main/java/com/vanguard/api/client/` (extend `BaseApiClient`)
- a record DTO → `src/main/java/com/vanguard/api/dto/`
- a JSON schema → `src/main/resources/schemas/`
- steps → `src/test/java/com/vanguard/api/steps/` · features → `src/test/resources/features/`
- a fake endpoint → `vanguard-core/src/main/resources/wiremock/mappings/`

Clients, DTOs and the ready-made steps (`ApiCommonSteps`) are in `src/main`, so the E2E and load modules can reuse them.
