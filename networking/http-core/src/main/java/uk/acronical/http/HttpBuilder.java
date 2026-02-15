package uk.acronical.http;

import com.google.gson.Gson;
import uk.acronical.common.LoggerUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class HttpBuilder {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).connectTimeout(Duration.ofSeconds(10)).build();

    private static final Gson gson = new Gson();

    private final String url;
    public HttpRequest.Builder builder;

    /**
     * Creates a new HttpResponseWrapper with the specified URL.
     *
     * @param url The URL to which the HTTP request will be sent.
     */
    public HttpBuilder(String url) {
        this.url = url;
        this.builder = HttpRequest.newBuilder().uri(URI.create(url));
    }

    /**
     * Adds a header to the HTTP request.
     *
     * @param key The header name.
     * @param value The header value.
     * @return A new HttpBuilder instance for chaining further configurations.
     */
    public HttpBuilder header(String key, String value) {
        builder.header(key, value);
        return this;
    }

    /**
     * Sets the body of the HTTP request with the provided content. The content is sent as a JSON string.
     *
     * @param content The content to be sent in the body of the HTTP request.
     * @return A new HttpBuilder instance for chaining further configurations.
     */
    public HttpBuilder body(String content) {
        builder.header("Content-Type", "application/json");
        builder.method("POST", HttpRequest.BodyPublishers.ofString(content));
        return this;
    }

    /**
     * Sets the body of the HTTP request with the provided content. The content is converted to a JSON string using Gson.
     *
     * @param content The content to be sent in the body of the HTTP request, which will be converted to JSON.
     * @return A new HttpBuilder instance for chaining further configurations.
     */
    public HttpBuilder body(Object content) {
        return body(gson.toJson(content));
    }

    /**
     * Sends the HTTP request asynchronously and returns a CompletableFuture that will complete with an HttpResponseWrapper containing the response.
     *
     * @return A CompletableFuture that will complete with an HttpResponseWrapper containing the response from the HTTP request.
     */
    public CompletableFuture<HttpResponseWrapper> get() {
        builder.GET();
        return sendAsync();
    }

    /**
     * Sends the HTTP request asynchronously and returns a CompletableFuture that will complete with an HttpResponseWrapper containing the response. If the method is not set to POST, it defaults to POST with no body.
     *
     * @return A CompletableFuture that will complete with an HttpResponseWrapper containing the response from the HTTP request.
     */
    public CompletableFuture<HttpResponseWrapper> post() {
        if (builder.build().method().equalsIgnoreCase("GET")) builder.POST(HttpRequest.BodyPublishers.noBody());
        return sendAsync();
    }

    /**
     * Sends the HTTP request asynchronously and returns a CompletableFuture that will complete with an HttpResponseWrapper containing the response.
     *
     * @return A CompletableFuture that will complete with an HttpResponseWrapper containing the response from the HTTP request.
     */
    private CompletableFuture<HttpResponseWrapper> sendAsync() {
        return client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponseWrapper::new).exceptionally(ex -> {
            LoggerUtils.severe("Failed to send HTTP request to " + url + ": " + ex.getMessage());
            return null;
        });
    }
}
