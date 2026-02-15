package uk.acronical.http;

import java.net.http.HttpRequest;

public class Http {

    /**
     * Creates a new HttpBuilder with the specified URL.
     *
     * @param url The URL to which the HTTP request will be sent.
     * @return A new HttpBuilder instance for chaining further configurations.
     */
    public static HttpBuilder request(String url) {
        return new HttpBuilder(url);
    }

    /**
     * Creates a new HttpBuilder with the specified URL and sets the HTTP method to GET.
     *
     * @param url The URL to which the HTTP request will be sent.
     * @return A new HttpBuilder instance for chaining further configurations.
     */
    public static HttpBuilder get(String url) {
        HttpBuilder builder = new HttpBuilder(url);
        builder.builder.GET();
        return builder;
    }

    /**
     * Creates a new HttpBuilder with the specified URL and sets the HTTP method to POST.
     *
     * @param url The URL to which the HTTP request will be sent.
     * @return A new HttpBuilder instance for chaining further configurations.
     */
    public static HttpBuilder post(String url) {
        HttpBuilder builder = new HttpBuilder(url);
        builder.builder.POST(HttpRequest.BodyPublishers.noBody());
        return builder;
    }

    /**
     * Creates a new HttpBuilder with the specified URL and sets the HTTP method to PUT.
     *
     * @param url The URL to which the HTTP request will be sent.
     * @return A new HttpBuilder instance for chaining further configurations.
     */
    public static HttpBuilder put(String url) {
        HttpBuilder builder = new HttpBuilder(url);
        builder.builder.PUT(HttpRequest.BodyPublishers.noBody());
        return builder;
    }

    /**
     * Creates a new HttpBuilder with the specified URL and sets the HTTP method to DELETE.
     *
     * @param url The URL to which the HTTP request will be sent.
     * @return A new HttpBuilder instance for chaining further configurations.
     */
    public static HttpBuilder delete(String url) {
        HttpBuilder builder = new HttpBuilder(url);
        builder.builder.DELETE();
        return builder;
    }

}
