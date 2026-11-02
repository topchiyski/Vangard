# vanguard-api

- Test command: `mvn verify -pl vanguard-api,vanguard-reporting -am -Dcucumber.filter.tags="@tag"`.
- Clients extend `BaseApiClient` and use its `get/post/put/delete`, so responses land in `ContextKey.LAST_RESPONSE`.
- `ApiCommonSteps`, clients, DTOs and schemas are in `src/main` on purpose: e2e and load reuse them. Business steps go in `src/test`.
- DTOs are records with `@JsonInclude(NON_NULL)` + `@JsonIgnoreProperties(ignoreUnknown = true)`.
- The sample API is WireMock. Its stubs live in `vanguard-core/src/main/resources/wiremock/`, not here.
