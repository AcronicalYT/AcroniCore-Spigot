package uk.acronical.updater;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a numeric version string and provides comparison logic.
 * <p>
 * This class facilitates version checking by splitting version strings
 * into numeric segments and comparing them sequentially.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class Version implements Comparable<Version> {

    private final String version;

    /**
     * Initialises a new {@link Version} instance.
     *
     * @param version The version string (e.g., {@code "1.0.2"}).
     * @throws IllegalArgumentException If the format does not follow a numeric dot-separated pattern.
     */
    public Version(@NotNull String version) {
        if (!version.matches("\\d+(\\.\\d+)*")) throw new IllegalArgumentException("Invalid version format: " + version);
        this.version = version;
    }

    /**
     * Compares this version to another to determine their chronological order.
     * <p>
     * The comparison processes each segment of the version string numerically.
     * If one version has fewer segments than the other, the missing segments
     * are treated as zero.
     *
     * @param other The {@link Version} to compare against.
     * @return A negative integer, zero, or a positive integer as this version
     * is less than, equal to, or greater than the specified version.
     */
    @Override
    public int compareTo(@NotNull Version other) {
        String[] thisParts = this.getParts();
        String[] otherParts = other.getParts();

        int length = Math.max(thisParts.length, otherParts.length);

        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int otherPart = i < otherParts.length ? Integer.parseInt(otherParts[i]) : 0;

            if (thisPart < otherPart) return -1;
            if (thisPart > otherPart) return 1;
        }

        return 0;
    }

    /**
     * Retrieves the raw version string.
     *
     * @return The version string.
     */
    @NotNull
    public String get() {
        return this.version;
    }

    /**
     * Splits the version into its numeric components.
     */
    private String[] getParts() {
        return this.version.split("\\.");
    }
}
