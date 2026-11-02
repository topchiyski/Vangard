package com.vanguard.core.context;

/**
 * Names for values shared between steps of ONE scenario.
 *
 * <p>Need a new key? Add it here with a one-line comment saying what it holds.
 */
public enum ContextKey {
    /** The last RestAssured {@code Response}. Set automatically by every API client call. */
    LAST_RESPONSE,
    /** A {@code UserDto} created during the scenario. */
    CREATED_USER,
    /** The product list returned by the Catalog API. */
    CATALOG,
    /** The product chosen during an E2E journey. */
    SELECTED_PRODUCT,
    /** The item total shown at checkout. */
    ORDER_TOTAL,
    /** The {@code LoadProfile} configured by Given-steps of a load test. */
    LOAD_PROFILE,
    /** The {@code LoadResult} of the last load test run. */
    LOAD_RESULT,
    /** The name of the current Cucumber scenario. Set automatically by hooks. */
    SCENARIO_NAME
}
