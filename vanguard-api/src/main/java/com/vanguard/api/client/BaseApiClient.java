package com.vanguard.api.client;

import com.vanguard.core.config.Endpoints;
import com.vanguard.core.context.ContextKey;
import com.vanguard.core.context.ScenarioContext;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Parent of every API client. Extend it and add one method per endpoint:
 *
 * <pre>{@code
 * public class UserClient extends BaseApiClient {
 *     public UserClient(ScenarioContext context) { super(context); }
 *     public Response getUser(int id) { return get("/users/{id}", id); }
 * }
 * }</pre>
 *
 * <p>Every call automatically:
 * <ul>
 *   <li>uses the configured base URL (WireMock by default),</li>
 *   <li>sends and accepts JSON,</li>
 *   <li>attaches the request and response to the Allure report,</li>
 *   <li>logs request + response to the console if an assertion on it fails,</li>
 *   <li>stores the response in {@link ScenarioContext} under {@link ContextKey#LAST_RESPONSE}.</li>
 * </ul>
 */
public abstract class BaseApiClient {

    private final ScenarioContext context;
    private RequestSpecification spec;

    protected BaseApiClient(ScenarioContext context) {
        this.context = context;
    }

    /**
     * GET a path. Path parameters fill {placeholders}: {@code get("/users/{id}", 1)}.
     */
    protected Response get(String path, Object... pathParams) {
        return remember(request().get(path, pathParams));
    }

    /**
     * POST an object (serialized to JSON) or a JSON string.
     */
    protected Response post(String path, Object body, Object... pathParams) {
        return remember(request().body(body).post(path, pathParams));
    }

    /**
     * PUT an object (serialized to JSON) or a JSON string.
     */
    protected Response put(String path, Object body, Object... pathParams) {
        return remember(request().body(body).put(path, pathParams));
    }

    /**
     * DELETE a path.
     */
    protected Response delete(String path, Object... pathParams) {
        return remember(request().delete(path, pathParams));
    }

    /**
     * A request with all the defaults above. Use it for anything unusual (headers, query params...).
     */
    protected RequestSpecification request() {
        if (spec == null) {
            spec = new RequestSpecBuilder()
                    .setBaseUri(Endpoints.apiBaseUrl())
                    .setContentType(ContentType.JSON)
                    .setAccept(ContentType.JSON)
                    .addFilter(new AllureRestAssured())
                    .build();
        }
        return given().spec(spec).log().ifValidationFails();
    }

    private Response remember(Response response) {
        context.set(ContextKey.LAST_RESPONSE, response);
        return response;
    }
}
