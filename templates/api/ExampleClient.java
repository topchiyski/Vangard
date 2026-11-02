package com.vanguard.templates.api;

import com.vanguard.api.client.BaseApiClient;
import com.vanguard.core.context.ScenarioContext;
import io.restassured.response.Response;

/**
 * TEMPLATE: API client.
 * Copy to vanguard-api/src/main/java/com/vanguard/api/client/<Name>Client.java and:
 * 1. TODO rename (e.g. OrderClient); package com.vanguard.api.client
 * 2. TODO one method per endpoint, named after what it does
 * 3. TODO return the Response; steps decide what to assert
 * Every call is logged to Allure and stored in ScenarioContext (LAST_RESPONSE) automatically.
 */
public class ExampleClient extends BaseApiClient {

    public ExampleClient(ScenarioContext context) {
        super(context);
    }

    /** GET /todos/{id} - TODO */
    public Response getTodo(int id) {
        return get("/todos/{id}", id);
    }

    /** POST /todos - TODO */
    public Response createTodo(ExampleDto todo) {
        return post("/todos", todo);
    }
}
