package uk.acronical.error.upload;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;
import uk.acronical.http.Http;
import uk.acronical.http.HttpBuilder;

import java.util.concurrent.CompletableFuture;

/**
 * A utility for uploading text content to a Pastefy-based service.
 * <p>
 * This class facilitates the creation of unlisted pastes, making it ideal
 * for sharing stack traces or error logs through the Pastefy API.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class PastefyUploader {

    private final String baseUrl;

    /**
     * Initialises the uploader with a target domain.
     *
     * @param domain The domain of the Pastefy instance (e.g., {@code "https://pastefy.app"}).
     */
    public PastefyUploader(@NotNull String domain) {
        this.baseUrl = domain.endsWith("/") ? domain.substring(0, domain.length() - 1) : domain;;
    }

    /**
     * Uploads content asynchronously as an unlisted paste.
     * <p>
     * This method utilises the {@code /api/v2/paste} endpoint. If the upload is
     * successful, the returned {@link CompletableFuture} provides the full
     * URL to the created paste.
     *
     * @param title   The title of the paste.
     * @param content The text content to upload.
     * @return A {@link CompletableFuture} containing the paste URL, or {@code null} if the upload fails.
     */
    @NotNull
    public CompletableFuture<String> upload(@NotNull String title, @NotNull String content) {
        JsonObject payload = new JsonObject();
        payload.addProperty("title", title);
        payload.addProperty("content", content);
        payload.addProperty("type", "PASTE");
        payload.addProperty("visibility", "UNLISTED");

        HttpBuilder request = Http.post(baseUrl + "/api/v2/paste").body(payload.toString());

        return request.post().thenApply(response -> {
            if (!response.isSuccessful()) {
                LoggerUtils.warn("Failed to upload paste: " + response.getStatusCode() + " - " + response.getBody());
                return null;
            }

            JsonObject jsonResponse = response.getAsJson();
            if (!jsonResponse.has("paste")) return null;

            String id = jsonResponse.getAsJsonObject("paste").get("id").getAsString();
            return baseUrl + "/" + id;
        });
    }
}
