package uk.acronical.updater;

import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;
import uk.acronical.http.Http;

import java.util.function.Consumer;

/**
 * A utility for checking for plugin updates via GitHub Releases or the SpigotMC API.
 * <p>
 * This class fetches remote version strings asynchronously and compares them against
 * the local version using the {@link Version} utility.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class UpdateChecker {

    private final String currentVersion;

    private static final String GITHUB_API_URL = "https://api.github.com/repos/%s/%s/releases/latest";
    private static final String SPIGOT_API_URL = "https://api.spigotmc.org/legacy/update.php?resource=%s";

    /**
     * Initialises the {@link UpdateChecker} using the version defined in the {@code plugin.yml}.
     *
     * @param plugin The plugin instance to check.
     */
    public UpdateChecker(@NotNull Plugin plugin) {
        this.currentVersion = plugin.getDescription().getVersion();
    }

    /**
     * Checks for the latest release on GitHub.
     * <p>
     * This method automatically strips the 'v' prefix from GitHub tags (e.g., {@code v1.0.0}
     * becomes {@code 1.0.0}) to ensure compatibility with the {@link Version} class.
     *
     * @param user     The GitHub username or organisation.
     * @param repo     The repository name.
     * @param callback A consumer to handle the {@link UpdateResult}.
     */
    public void checkGitHub(@NotNull String user, @NotNull String repo, @NotNull Consumer<UpdateResult> callback) {
        String url = String.format(GITHUB_API_URL, user, repo);

        Http.get(url).get().thenAccept(response -> {
            if (!response.isSuccessful()) {
                LoggerUtils.warn("Failed to check for updates on GitHub: " + response.getStatusCode());
                return;
            }

            JsonObject json = response.getAsJson();
            String latestVersionTag = json.get("tag_name").getAsString().replace("v", "");
            String downloadUrl = json.get("html_url").getAsString();
            compareAndCallback(latestVersionTag, downloadUrl, callback);
        });
    }

    /**
     * Checks for the latest version on SpigotMC.
     *
     * @param resourceId The numerical ID of the resource on SpigotMC.
     * @param callback   A consumer to handle the {@link UpdateResult}.
     */
    public void checkSpigot(int resourceId, @NotNull Consumer<UpdateResult> callback) {
        String url = String.format(SPIGOT_API_URL, resourceId);

        Http.get(url).get().thenAccept(response -> {
            if (!response.isSuccessful()) {
                LoggerUtils.warn("Failed to check for updates on Spigot: " + response.getStatusCode());
                return;
            }

            String latestVersion = response.getBody().trim();
            String downloadUrl = "https://www.spigotmc.org/resources/" + resourceId + "/";
            compareAndCallback(latestVersion, downloadUrl, callback);
        });
    }

    /**
     * Compares the remote version with the current local version.
     */
    private void compareAndCallback(String latestVersion, String downloadUrl, Consumer<UpdateResult> callback) {
        try {
            Version current = new Version(currentVersion);
            Version latest = new Version(latestVersion);

            if (latest.compareTo(current) > 0) {
                if (downloadUrl == null) downloadUrl = "Unabel to retrieve download URL, please check where you downloaded the plugin for updates.";
                callback.accept(new UpdateResult(true, latestVersion, currentVersion, downloadUrl));
            } else {
                callback.accept(new UpdateResult(false, latestVersion, currentVersion, downloadUrl));
            }
        } catch (IllegalArgumentException e) {
            LoggerUtils.warn("Could not parse version numbers: " + e.getMessage());
        }
    }

    /**
     * Represents the outcome of an update check.
     *
     * @param hasUpdate  Whether a newer version exists remotely.
     * @param newVersion The version string found on the remote server.
     * @param oldVersion The current version of the local plugin.
     */
    public record UpdateResult(boolean hasUpdate, String newVersion, String oldVersion, String downloadUrl) {}
}
