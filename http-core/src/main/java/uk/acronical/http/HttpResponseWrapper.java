package uk.acronical.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.http.HttpResponse;

public class HttpResponseWrapper {

    private final HttpResponse<String> rawResponse;
    private static final Gson gson = new Gson();

    /**
     * Creates a new HttpResponseWrapper with the specified raw HTTP response.
     *
     * @param rawResponse The raw HTTP response to be wrapped.
     */
    public HttpResponseWrapper(HttpResponse<String> rawResponse) {
        this.rawResponse = rawResponse;
    }

    /**
     * Returns whether the HTTP response indicates a successful request (status code in the range 200-299).
     *
     * @return true if the response is successful, false otherwise.
     */
    public boolean isSuccessful() {
        int statusCode = rawResponse.statusCode();
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Returns the status code of the HTTP response.
     *
     * @return The status code of the HTTP response.
     */
    public int getStatusCode() {
        return rawResponse.statusCode();
    }

    /**
     * Returns the body of the HTTP response as a string.
     *
     * @return The body of the HTTP response.
     */
    public String getBody() {
        return rawResponse.body();
    }

    /**
     * Parses the body of the HTTP response as a JSON object and returns it.
     *
     * @return The body of the HTTP response as a JsonObject, or an empty JsonObject if the body is null or empty.
     */
    public JsonObject getAsJson() {
        if (getBody() == null || getBody().isEmpty()) return new JsonObject();
        return gson.fromJson(getBody(), JsonObject.class);
    }

    /**
     * Parses the body of the HTTP response as an object of the specified class and returns it.
     *
     * @param clazz The class to which the body of the HTTP response should be parsed.
     * @param <T> The type of the object to be returned.
     * @return The body of the HTTP response parsed as an object of the specified class.
     */
    public <T> T getAsObject(Class<T> clazz) {
        return gson.fromJson(getBody(), clazz);
    }
}
