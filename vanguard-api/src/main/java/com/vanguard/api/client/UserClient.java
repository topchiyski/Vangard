package com.vanguard.api.client;

import com.vanguard.api.dto.UserDto;
import com.vanguard.core.context.ScenarioContext;
import io.restassured.response.Response;

/**
 * REFERENCE EXAMPLE of an API client: one small method per endpoint, named after what it does.
 * Copy this class (or templates/api/ExampleClient.java) to add a new API.
 */
public class UserClient extends BaseApiClient {

    public UserClient(ScenarioContext context) {
        super(context);
    }

    /**
     * GET /users/{id}
     */
    public Response getUser(int id) {
        return get("/users/{id}", id);
    }

    /**
     * POST /users with the user as JSON.
     */
    public Response createUser(UserDto user) {
        return post("/users", user);
    }

    /**
     * PUT /users/{id} with the user as JSON.
     */
    public Response updateUser(int id, UserDto user) {
        return put("/users/{id}", user, id);
    }

    /**
     * DELETE /users/{id}
     */
    public Response deleteUser(int id) {
        return delete("/users/{id}", id);
    }
}
