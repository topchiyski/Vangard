package com.vanguard.templates.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * TEMPLATE: request/response body as a Java record.
 * Copy to vanguard-api/src/main/java/com/vanguard/api/dto/<Name>Dto.java and:
 * 1. TODO rename (e.g. OrderDto); package com.vanguard.api.dto
 * 2. TODO one field per JSON property, same names as the JSON (or use @JsonProperty("json_name"))
 * 3. TODO use BigDecimal for money, Integer (not int) for fields that may be missing
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExampleDto(Integer id, String title, Boolean completed) {
}
