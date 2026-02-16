package uk.acronical.http;

import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;

/**
 * A static factory class for initiating HTTP requests.
 * <p>
 * This class provides a simplified interface for creating {@link HttpBuilder}
 * instances with pre-configured HTTP methods.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class Http {

    /**
     * Initialises a new {@link HttpBuilder} with the specified URL.
     *
     * @param url The target URL for the HTTP request.
     * @return A new {@link HttpBuilder} instance for further configuration.
     */
    public static HttpBuilder request(@NotNull String url) {
        return new HttpBuilder(url);
    }

    /**
     * Initialises a new {@link HttpBuilder} pre-configured with the {@code GET} method.
     *
     * @param url The target URL for the request.
     * @return A new {@link HttpBuilder} instance.
     */
    public static HttpBuilder get(@NotNull String url) {
        HttpBuilder builder = new HttpBuilder(url);
        builder.builder.GET();
        return builder;
    }

    /**
     * Initialises a new {@link HttpBuilder} pre-configured with the {@code POST} method.
     * <p>
     * By default, this is initialised with an empty body publisher.
     *
     * @param url The target URL for the request.
     * @return A new {@link HttpBuilder} instance.
     */
    public static HttpBuilder post(@NotNull String url) {
        HttpBuilder builder = new HttpBuilder(url);
        builder.builder.POST(HttpRequest.BodyPublishers.noBody());
        return builder;
    }

    /**
     * Initialises a new {@link HttpBuilder} pre-configured with the {@code PUT} method.
     * <p>
     * By default, this is initialised with an empty body publisher.
     *
     * @param url The target URL for the request.
     * @return A new {@link HttpBuilder} instance.
     */
    public static HttpBuilder put(@NotNull String url) {
        HttpBuilder builder = new HttpBuilder(url);
        builder.builder.PUT(HttpRequest.BodyPublishers.noBody());
        return builder;
    }

    /**
     * Initialises a new {@link HttpBuilder} pre-configured with the {@code DELETE} method.
     *
     * @param url The target URL for the request.
     * @return A new {@link HttpBuilder} instance.
     */
    public static HttpBuilder delete(@NotNull String url) {
        HttpBuilder builder = new HttpBuilder(url);
        builder.builder.DELETE();
        return builder;
    }
}
