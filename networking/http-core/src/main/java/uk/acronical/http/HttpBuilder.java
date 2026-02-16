package uk.acronical.http;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * A builder for configuring and executing asynchronous HTTP requests.
 * <p>
 * This class wraps the standard {@link HttpClient} to provide a fluent API,
 * including automatic JSON serialisation via {@link Gson} and integrated
 * error logging.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class HttpBuilder {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).connectTimeout(Duration.ofSeconds(10)).build();

    private static final Gson gson = new Gson();

    private final String url;
    public HttpRequest.Builder builder;

    /**
     * Initialises a new {@link HttpBuilder} for the target URL.
     *
     * @param url The URL to which the HTTP request will be sent.
     */
    public HttpBuilder(@NotNull String url) {
        this.url = url;
        this.builder = HttpRequest.newBuilder().uri(URI.create(url));
    }

    /**
     * Appends a header to the HTTP request.
     *
     * @param key   The header name (e.g., {@code "Authorization"}).
     * @param value The value associated with the header.
     * @return The current {@link HttpBuilder} instance for method chaining.
     */
    public HttpBuilder header(@NotNull String key, @NotNull String value) {
        builder.header(key, value);
        return this;
    }

    /**
     * Configures the request body with a raw JSON string.
     * <p>
     * This method automatically sets the {@code Content-Type} to {@code application/json}
     * and switches the request method to {@code POST}.
     *
     * @param content The JSON string to be sent.
     * @return The current {@link HttpBuilder} instance for method chaining.
     */
    public HttpBuilder body(@NotNull String content) {
        builder.header("Content-Type", "application/json");
        builder.method("POST", HttpRequest.BodyPublishers.ofString(content));
        return this;
    }

    /**
     * Serialises the provided object to JSON and sets it as the request body.
     *
     * @param content The object to be serialised via {@link Gson}.
     * @return The current {@link HttpBuilder} instance for method chaining.
     */
    public HttpBuilder body(@NotNull Object content) {
        return body(gson.toJson(content));
    }

    /**
     * Executes a {@code GET} request asynchronously.
     *
     * @return A {@link CompletableFuture} that completes with an {@link HttpResponseWrapper}.
     */
    public CompletableFuture<HttpResponseWrapper> get() {
        builder.GET();
        return sendAsync();
    }

    /**
     * Executes a {@code POST} request asynchronously.
     * <p>
     * If no body has been specified, an empty body publisher is utilised.
     *
     * @return A {@link CompletableFuture} that completes with an {@link HttpResponseWrapper}.
     */
    public CompletableFuture<HttpResponseWrapper> post() {
        if (builder.build().method().equalsIgnoreCase("GET")) builder.POST(HttpRequest.BodyPublishers.noBody());
        return sendAsync();
    }

    /**
     * Dispatches the request through the shared {@link HttpClient}.
     *
     * @return A {@link CompletableFuture} containing the wrapped response,
     * or {@code null} if an exception occurs.
     */
    private CompletableFuture<HttpResponseWrapper> sendAsync() {
        return client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponseWrapper::new).exceptionally(ex -> {
            LoggerUtils.severe("Failed to send HTTP request to " + url + ": " + ex.getMessage());
            return null;
        });
    }
}
