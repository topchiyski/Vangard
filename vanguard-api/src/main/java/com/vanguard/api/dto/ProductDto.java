package com.vanguard.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * A catalog product. Prices use {@link BigDecimal}: never use double for money.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductDto(int id, String name, BigDecimal price) {
}
