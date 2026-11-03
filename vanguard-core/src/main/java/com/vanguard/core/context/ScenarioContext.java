package com.vanguard.core.context;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shares data between the steps of ONE scenario.
 *
 * <p>Cucumber creates a fresh instance for every scenario and injects it into any step class,
 * page object or API client that asks for it in its constructor:
 *
 * <pre>{@code
 * public class UserSteps {
 *     private final ScenarioContext context;
 *     public UserSteps(ScenarioContext context) { this.context = context; }
 * }
 * }</pre>
 *
 * <p>Two kinds of storage:
 * <ul>
 *   <li><b>Typed keys</b> ({@link ContextKey}) for Java code: {@code set(CREATED_USER, user)}.</li>
 *   <li><b>Named values</b> for Gherkin: {@code And I remember "42" as "userId"}, later used as {@code {userId}}.</li>
 * </ul>
 */
public class ScenarioContext {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z0-9_.-]+)}");
    private static final String HELP = "See docs/ONBOARDING_GUIDE.md#recipe-5-share-data-between-steps";

    private final Map<ContextKey, Object> values = new EnumMap<>(ContextKey.class);
    private final Map<String, String> named = new LinkedHashMap<>();

    /** Stores a value. Overwrites any previous value for the same key. */
    public void set(ContextKey key, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot store null for " + key + ". Did the previous call fail?");
        }
        values.put(key, value);
    }

    /**
     * Reads a value with its type, e.g. {@code context.get(CREATED_USER, UserDto.class)}.
     *
     * @throws IllegalStateException with a hint when the key is missing or has another type
     */
    public <T> T get(ContextKey key, Class<T> type) {
        Object value = values.get(key);
        if (value == null) {
            throw new IllegalStateException("No value for ContextKey." + key + " in ScenarioContext. "
                    + "Available keys: " + values.keySet() + ". "
                    + "Did an earlier step forget to call context.set(ContextKey." + key + ", ...)? " + HELP);
        }
        if (!type.isInstance(value)) {
            throw new IllegalStateException("ContextKey." + key + " holds a " + value.getClass().getSimpleName()
                    + ", not a " + type.getSimpleName() + ". Check the class you pass to context.get(...). " + HELP);
        }
        return type.cast(value);
    }

    /** True when a value is stored for the key. */
    public boolean contains(ContextKey key) {
        return values.containsKey(key);
    }

    /** Stores a named value that Gherkin steps can reuse as {@code {name}}. */
    public void remember(String name, Object value) {
        named.put(name, String.valueOf(value));
    }

    /** Reads a named value stored with {@link #remember(String, Object)}. */
    public String recall(String name) {
        String value = named.get(name);
        if (value == null) {
            throw new IllegalStateException("Nothing remembered as \"" + name + "\". Remembered names: "
                    + named.keySet() + ". Add a step like: And I save the response field \"id\" as \"" + name + "\". " + HELP);
        }
        return value;
    }

    /**
     * Replaces every {@code {name}} in the text with its remembered value.
     * Common steps call this automatically; call it yourself in custom steps that accept text.
     */
    public String resolve(String text) {
        if (text == null || text.indexOf('{') < 0) {
            return text;
        }
        Matcher matcher = PLACEHOLDER.matcher(text);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1);
            // WHY: unknown placeholders are left untouched so JSON bodies with braces still work.
            String replacement = named.containsKey(name) ? named.get(name) : matcher.group(0);
            matcher.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(out);
        return out.toString();
    }
}
