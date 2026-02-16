package uk.acronical.error;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.acronical.common.LoggerUtils;
import uk.acronical.error.report.ErrorContext;
import uk.acronical.error.upload.PastefyUploader;

import java.util.function.Consumer;

/**
 * A centralised service for managing plugin errors and diagnostic uploads.
 * <p>
 * This service orchestrates the generation of detailed error reports and
 * handles their asynchronous upload to a Pastefy instance.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class ErrorService {

    private final Plugin plugin;
    private final PastefyUploader uploader;
    private final ErrorContext contextBuilder;

    /**
     * Initialises the {@link ErrorService}.
     *
     * @param plugin      The plugin instance to monitor.
     * @param pastefyUrl  The base URL for the Pastefy service used for uploads.
     */
    public ErrorService(@NotNull Plugin plugin, @NotNull String pastefyUrl) {
        this.plugin = plugin;
        this.uploader = new PastefyUploader(pastefyUrl);
        this.contextBuilder = new ErrorContext(plugin);
    }

    /**
     * Processes an exception by generating a report and uploading it.
     * <p>
     * This method logs the error locally before initiating an asynchronous
     * upload. Once complete, the optional callback is triggered with the
     * resulting URL.
     *
     * @param throwable The exception to handle.
     * @param comment   An optional description of the circumstances (may be {@code null}).
     * @param callback  An optional consumer to process the resulting URL (may be {@code null}).
     */
    public void handle(@NotNull Throwable throwable, @Nullable String comment, @Nullable Consumer<String> callback) {
        LoggerUtils.severe("An error occurred. Generating report...");
        if (comment != null && !comment.isEmpty()) LoggerUtils.severe("Context: " + comment);
        LoggerUtils.severe(throwable.getMessage());

        String fullReport = contextBuilder.generateReport(throwable, comment);

        uploader.upload("Error Report - " + plugin.getName(), fullReport).thenAccept(url -> {
            if (url != null) {
                LoggerUtils.severe("Error report uploaded: " + url);
                callback.accept(url);
            } else {
                LoggerUtils.severe("Failed to upload error report.");
                callback.accept(null);
            }
        });
    }

    /**
     * Processes an exception with no additional comment or callback.
     *
     * @param throwable The exception to handle.
     */
    public void handle(@NotNull Throwable throwable) {
        handle(throwable, null, null);
    }
}
