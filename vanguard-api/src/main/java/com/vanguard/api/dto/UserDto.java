package com.vanguard.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A user as the API sends and receives it. A Java {@code record}: immutable, with equals/toString for free.
 *
 * <p>{@code id} is {@code null} when creating a user (the server assigns it) and is then left out of the JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(Integer id, String name, String username, String email) {

    /** A user to create: no id yet. */
    public static UserDto newUser(String name, String username, String email) {
        return new UserDto(null, name, username, email);
    }
}
