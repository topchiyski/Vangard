# vanguard-api

- Test command: `mvn verify -pl vanguard-api,vanguard-reporting -am -Dcucumber.filter.tags="@tag"`.
- Clients extend `BaseApiClient` and call its `get/post/put/delete`, so responses land in `ContextKey.LAST_RESPONSE`.
<!-- 2026-11-02: Triggered by design: e2e and load reuse clients, DTOs and common steps. Prune when: never (design). -->
- Clients, DTOs, schemas and `ApiCommonSteps` belong in `src/main`; business steps in `src/test`.
- The sample API is WireMock; add stubs in `vanguard-core/src/main/resources/wiremock/`.
