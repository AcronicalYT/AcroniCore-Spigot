package uk.acronical.punishment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a disciplinary action taken against a player.
 * <p>
 * This model encapsulates all data regarding a punishment, including its duration,
 * reasoning, and the parties involved. It is designed to be easily serialised
 * for database persistence.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class Punishment {

    private final UUID id;
    private final UUID target;
    private final UUID issuer;
    private final PunishmentType type;
    private final String reason;
    private final long issueTime;
    private final long expiryTime;

    /**
     * Initialises a new punishment record.
     *
     * @param target         The {@link UUID} of the player receiving the punishment.
     * @param issuer         The {@link UUID} of the staff member (or null if console).
     * @param type           The {@link PunishmentType} (e.g. BAN, MUTE).
     * @param reason         The explanation for the action.
     * @param durationMillis The duration in milliseconds. Use {@code -1} for permanent.
     */
    public Punishment(@NotNull UUID target, @Nullable UUID issuer, @NotNull PunishmentType type, @Nullable String reason, long durationMillis) {
        this.id = UUID.randomUUID();
        this.target = target;
        this.issuer = issuer;
        this.type = type;
        this.reason = reason != null ? reason : "No reason specified.";
        this.issueTime = System.currentTimeMillis();
        this.expiryTime = durationMillis <= 0 ? -1 : this.issueTime + durationMillis;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public UUID getTarget() {
        return target;
    }

    @Nullable
    public UUID getIssuer() {
        return issuer;
    }

    @NotNull
    public PunishmentType getType() {
        return type;
    }

    @NotNull
    public String getReason() {
        return reason;
    }

    public long getIssueTime() {
        return issueTime;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    /**
     * Verifies if the punishment has reached its expiration date.
     *
     * @return {@code true} if the current time is beyond the expiry time.
     */
    public boolean isExpired() {
        if (expiryTime == -1) return false;
        return System.currentTimeMillis() > expiryTime;
    }
}
