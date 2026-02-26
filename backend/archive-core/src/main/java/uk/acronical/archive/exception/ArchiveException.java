package uk.acronical.archive.exception;

import org.jetbrains.annotations.NotNull;
import uk.acronical.exception.AcroniCoreException;
import java.io.File;

/**
 * Thrown when an error occurs during the archival or extraction of files.
 * <p>
 * This exception is utilised to handle failures in file-system operations,
 * such as zip compression, directory backups, or log rotation, providing
 * clear visibility of the target file.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class ArchiveException extends AcroniCoreException {

    /**
     * Initialises a new archive exception for a specific file.
     *
     * @param archive The {@link File} involved in the failure.
     * @param reason  A descriptive reason for the failure.
     */
    public ArchiveException(@NotNull File archive, @NotNull String reason) {
        super(String.format("Archive Error | File: [%s] | Reason: %s",
                archive.getName(), reason));
    }

    /**
     * Initialises a new archive exception with an underlying cause.
     *
     * @param archive The {@link File} involved in the failure.
     * @param reason  A descriptive reason for the failure.
     * @param cause   The underlying cause (e.g., an IOException).
     */
    public ArchiveException(@NotNull File archive, @NotNull String reason, @NotNull Throwable cause) {
        super(String.format("Archive Error | File: [%s] | Reason: %s",
                archive.getName(), reason), cause);
    }
}