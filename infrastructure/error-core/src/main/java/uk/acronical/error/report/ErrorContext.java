package uk.acronical.error.report;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A utility for generating detailed diagnostic reports from {@link Throwable} instances.
 * <p>
 * This class gathers environmental data, server specifications, and plugin metadata
 * to provide a comprehensive context for debugging.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class ErrorContext {

    private final Plugin plugin;

    /**
     * Initialises a new {@link ErrorContext} for the specified plugin.
     *
     * @param plugin The plugin instance to associate with the error reports.
     */
    public ErrorContext(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Generates a formatted error report string.
     * <p>
     * The report includes a timestamp, environmental variables (Java/OS),
     * server versioning, and the full stack trace of the provided error.
     *
     * @param throwable The exception or error to document.
     * @param comment   An optional descriptive comment to include in the report header.
     * @return A complete, formatted diagnostic report.
     */
    @NotNull
    public String generateReport(@NotNull Throwable throwable, @Nullable String comment) {
        StringBuilder report = new StringBuilder();

        report.append("--- AcroniCore Error Report ---\n");
        report.append("Generated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");

        if (comment != null && !comment.isEmpty()) report.append("Comment: ").append(comment).append("\n");
        report.append("\n");

        report.append("--- Environment ---\n");
        report.append("Server: ").append(Bukkit.getName()).append(" ").append(Bukkit.getServer().getVersion()).append("\n");
        report.append("Bukkit Version: ").append(Bukkit.getBukkitVersion()).append("\n");
        report.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        report.append("OS: ").append(System.getProperty("os.name")).append(" (").append(System.getProperty("os.arch")).append(")\n");
        report.append("\n");

        report.append("--- Plugin ---\n");
        report.append("Name: ").append(plugin.getName()).append("\n");
        report.append("Version: ").append(plugin.getDescription().getVersion()).append("\n");
        report.append("\n");

        report.append("--- Stack Trace ---\n");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        report.append(stringWriter);

        return report.toString();
    }
}
