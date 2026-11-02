package com.vanguard.api.client;

import com.vanguard.core.context.ScenarioContext;
import io.restassured.response.Response;

/**
 * Sends any HTTP request by method name. Powers the no-code common steps
 * ({@code When I send a GET request to "/users/1"}). For real tests prefer a dedicated client
 * such as {@link UserClient}: it reads like business language.
 */
public class GenericApiClient extends BaseApiClient {

    public GenericApiClient(ScenarioContext context) {
        super(context);
    }

    /**
     * @param method GET, POST, PUT, PATCH or DELETE (any case)
     * @param path   path relative to the API base URL
     * @param body   JSON body, or {@code null} for none
     */
    public Response send(String method, String path, String body) {
        return switch (method.toUpperCase()) {
            case "GET" -> get(path);
            case "DELETE" -> delete(path);
            case "POST" -> post(path, body);
            case "PUT" -> put(path, body);
            case "PATCH" -> request().body(body).patch(path);
            default -> throw new IllegalArgumentException("Unsupported HTTP method '" + method
                    + "'. Use GET, POST, PUT, PATCH or DELETE.");
        };
    }
}
