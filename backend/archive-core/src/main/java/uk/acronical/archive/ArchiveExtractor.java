package uk.acronical.archive;

import org.jetbrains.annotations.NotNull;
import uk.acronical.archive.exception.ArchiveException;
import uk.acronical.exception.impl.ValidationException;
import uk.acronical.task.TaskManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A service for asynchronously extracting ZIP archives.
 * <p>
 * This utility offloads file IO operations to a background thread via {@link TaskManager}
 * and includes security checks to prevent directory traversal attacks.
 *
 * @author Acronical
 * @since 1.0.4
 */
public class ArchiveExtractor {

    private final TaskManager taskManager;

    /**
     * Initialises the {@link ArchiveExtractor}.
     *
     * @param taskManager The task manager used to handle asynchronous execution.
     */
    public ArchiveExtractor(@NotNull TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    /**
     * Extracts a ZIP file to a specified destination directory.
     *
     * @param zip                  The source ZIP file.
     * @param destinationDirectory The directory where files should be extracted.
     * @return A {@link CompletableFuture} returning {@code true} if successful.
     */
    @NotNull
    public CompletableFuture<Boolean> extractZip(@NotNull File zip, @NotNull File destinationDirectory) {
        return taskManager.supplyAsync(() -> {
            if (!destinationDirectory.exists() && !destinationDirectory.mkdirs()) throw new ArchiveException(zip, "Failed to create destination directory: " + destinationDirectory.getAbsolutePath());

            try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zip))) {
                ZipEntry zipEntry = inputStream.getNextEntry();

                while (zipEntry != null) {
                    File newFile = resolveFile(destinationDirectory, zipEntry);

                    if (zipEntry.isDirectory()) {
                        if (!newFile.isDirectory() && !newFile.mkdirs()) throw new ArchiveException(zip, "Failed to create nested directory: " + newFile.getAbsolutePath());
                    } else {
                        File parent = newFile.getParentFile();

                        if (!parent.isDirectory() && !parent.mkdirs()) throw new ArchiveException(zip, "Failed to create parent directory for file: " + parent.getAbsolutePath());;

                        try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                fileOutputStream.write(buffer, 0, length);
                            }
                        }
                    }

                    zipEntry = inputStream.getNextEntry();
                }

                inputStream.closeEntry();
                return true;
            } catch (IOException e) {
                throw new ArchiveException(zip, "I/O error encountered during extraction", e);
            }
        });
    }

    /**
     * Safely resolves a file path within the target directory.
     * <p>
     * This method prevents "Zip Slip" traversal attacks by verifying the
     * canonical path of the entry.
     *
     * @param destinationDirectory The root extraction folder.
     * @param zipEntry             The entry to resolve.
     * @return The validated {@link File}.
     * @throws IOException If the entry attempts to escape the destination directory.
     */
    @NotNull
    private File resolveFile(@NotNull File destinationDirectory, @NotNull ZipEntry zipEntry) throws IOException {
        File destinationFile = new File(destinationDirectory, zipEntry.getName());

        String destinationDirectoryPath = destinationDirectory.getCanonicalPath();
        String destinationFilePath = destinationFile.getCanonicalPath();

        if (!destinationFilePath.startsWith(destinationDirectoryPath + File.separator)) throw new ValidationException("Security Alert: ZIP entry attempted to write outside target directory (Zip Slip): " + zipEntry.getName());

        return destinationFile;
    }
}
