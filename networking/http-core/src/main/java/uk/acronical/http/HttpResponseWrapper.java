package uk.acronical.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpResponse;

/**
 * A wrapper for {@link HttpResponse} that provides convenience methods for
 * processing response data and status codes.
 * <p>
 * This utility integrates with {@link Gson} to simplify the conversion of
 * response bodies into JSON objects or custom Java types.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class HttpResponseWrapper {

    private final HttpResponse<String> rawResponse;
    private static final Gson gson = new Gson();

    /**
     * Initialises a new {@link HttpResponseWrapper} with the raw response.
     *
     * @param rawResponse The {@link HttpResponse} received from the client.
     */
    public HttpResponseWrapper(@NotNull HttpResponse<String> rawResponse) {
        this.rawResponse = rawResponse;
    }

    /**
     * Determines if the request was successful based on the HTTP status code.
     * <p>
     * A request is considered successful if the status code falls within the
     * {@code 200-299} range.
     *
     * @return {@code true} if the request succeeded; otherwise {@code false}.
     */
    public boolean isSuccessful() {
        int statusCode = rawResponse.statusCode();
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Retrieves the numerical HTTP status code.
     *
     * @return The status code (e.g., 200, 404, 500).
     */
    public int getStatusCode() {
        return rawResponse.statusCode();
    }

    /**
     * Retrieves the raw response body as a string.
     *
     * @return The response body, or an empty string if none exists.
     */
    @NotNull
    public String getBody() {
        return rawResponse.body();
    }

    /**
     * Deserialises the response body into a {@link JsonObject}.
     *
     * @return A {@link JsonObject} containing the response data, or an empty
     * object if the body is null or malformed.
     */
    @NotNull
    public JsonObject getAsJson() {
        if (getBody() == null || getBody().isEmpty()) return new JsonObject();
        return gson.fromJson(getBody(), JsonObject.class);
    }

    /**
     * Deserialises the response body into an instance of the specified class.
     *
     * @param clazz The class type to map the JSON data to.
     * @param <T>   The resulting object type.
     * @return An instance of {@link T} populated with the response data.
     */
    @Nullable
    public <T> T getAsObject(@NotNull Class<T> clazz) {
        return gson.fromJson(getBody(), clazz);
    }
}
