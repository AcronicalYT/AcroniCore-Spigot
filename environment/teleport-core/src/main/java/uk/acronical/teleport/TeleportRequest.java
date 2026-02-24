package uk.acronical.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A builder-style request model for managing player teleportation.
 * <p>
 * This class facilitates the configuration of teleportation parameters, including
 * warm-up delays, movement-based cancellation, and safety verification.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class TeleportRequest {

    private final Player player;
    private final Location destination;

    private int delay = 0;
    private boolean cancelOnMove = true;
    public boolean checkSafe = true;

    private Consumer<Player> onSuccess;
    private Consumer<Player> onCancel;

    /**
     * Initialises a new teleport request.
     *
     * @param player      The {@link Player} to be teleported.
     * @param destination The target {@link Location}.
     */
    public TeleportRequest(@NotNull Player player, @NotNull Location destination) {
        this.player = player;
        this.destination = destination;
    }

    /**
     * Sets a warm-up period before the teleportation occurs.
     *
     * @param seconds The delay in seconds.
     * @return The current {@link TeleportRequest} instance.
     */
    public TeleportRequest delay(int seconds) {
        this.delay = seconds;
        return this;
    }

    /**
     * Toggles whether the teleport should be aborted if the player moves.
     *
     * @param cancel {@code true} to enable move-cancellation.
     * @return The current {@link TeleportRequest} instance.
     */
    public TeleportRequest cancelOnMove(boolean cancel) {
        this.cancelOnMove = cancel;
        return this;
    }

    /**
     * Toggles whether the destination should be checked for safety (e.g., lava or suffocation).
     *
     * @param check {@code true} to enable safety checks.
     * @return The current {@link TeleportRequest} instance.
     */
    public TeleportRequest checkSafe(boolean check) {
        this.checkSafe = check;
        return this;
    }

    /**
     * Defines an action to be executed upon a successful teleportation.
     *
     * @param action The callback consumer.
     * @return The current {@link TeleportRequest} instance.
     */
    public TeleportRequest onSuccess(@Nullable Consumer<Player> action) {
        this.onSuccess = action;
        return this;
    }

    /**
     * Defines an action to be executed if the teleportation is cancelled.
     *
     * @param action The callback consumer.
     * @return The current {@link TeleportRequest} instance.
     */
    public TeleportRequest onCancel(@Nullable Consumer<Player> action) {
        this.onCancel = action;
        return this;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public Location getDestination() {
        return destination;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isCancelOnMove() {
        return cancelOnMove;
    }

    @Nullable
    public Consumer<Player> getOnSuccess() {
        return onSuccess;
    }

    @Nullable
    public Consumer<Player> getOnCancel() {
        return onCancel;
    }
}
