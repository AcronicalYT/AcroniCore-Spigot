package uk.acronical.cooldown;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents a fixed-duration cooldown based on a future expiry timestamp.
 * <p>
 * This class utilises {@link Instant} to provide high-precision time tracking,
 * independent of server tick rates.
 *
 * @author Acronical
 * @since 1.0.1-SNAPSHOT
 */
public class Cooldown {

    private final Instant expiry;

    /**
     * Initialises a new cooldown that expires after the specified {@link Duration}.
     *
     * @param duration The length of time the cooldown should remain active.
     */
    public Cooldown(@NotNull Duration duration) {
        this.expiry = Instant.now().plus(duration);
    }

    /**
     * Checks if the cooldown is still in effect.
     *
     * @return {@code true} if the current time is before the expiry; otherwise {@code false}.
     */
    public boolean isActive() {
        return Instant.now().isBefore(expiry);
    }

    /**
     * Calculates the remaining time as a {@link Duration}.
     *
     * @return The {@link Duration} until expiry, or {@link Duration#ZERO} if finished.
     */
    @NotNull
    public Duration getRemaining() {
        if (!isActive()) return Duration.ZERO;
        return Duration.between(Instant.now(), expiry);
    }

    /**
     * Calculates the remaining time in milliseconds.
     *
     * @return The total milliseconds remaining until expiry.
     */
    public long getRemainingMillis() {
        return getRemaining().toMillis();
    }

    /**
     * Generates a human-readable string of the remaining time.
     * <p>
     * Displays minutes (e.g., {@code "2m"}) if more than 60 seconds remain,
     * otherwise displays seconds to one decimal place (e.g., {@code "14.5s"}).
     *
     * @return A formatted string representing the remaining time.
     */
    @NotNull
    public String getFormattedRemaining() {
        long millis = getRemainingMillis();
        if (millis >= 60000) {
            return (millis / 60000) + "m";
        } else {
            return String.format("%.1fs", millis / 1000.0);
        }
    }
}
