package uk.acronical.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A fluent builder utility for spawning and configuring {@link Particle} effects.
 * <p>
 * This class simplifies the interaction with Bukkit's particle API, allowing for
 * easy configuration of offsets, counts, and specialised particle data.
 *
 * @author Acronical
 * @since 1.0.2
 */
public class ParticleBuilder {

    private final Particle particle;
    private int count = 1;
    private double offsetX, offsetY, offsetZ, extra = 0;
    private Object data = null;

    /**
     * Initialises a new {@link ParticleBuilder} for a specific particle type.
     *
     * @param particle The {@link Particle} type to be spawned.
     */
    public ParticleBuilder(@NotNull Particle particle) {
        this.particle = particle;
    }

    /**
     * Sets the number of particles to be spawned.
     *
     * @param count The particle count.
     * @return The current {@link ParticleBuilder} instance.
     */
    public ParticleBuilder count(int count) {
        this.count = count;
        return this;
    }

    /**
     * Configures the randomisation offset for the particle's spawn position.
     *
     * @param x The maximum random offset on the X-axis.
     * @param y The maximum random offset on the Y-axis.
     * @param z The maximum random offset on the Z-axis.
     * @return The current {@link ParticleBuilder} instance.
     */
    public ParticleBuilder offset(double x, double y, double z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        return this;
    }

    /**
     * Sets the 'extra' data, usually representing the speed or colour intensity.
     *
     * @param extra The extra value.
     * @return The current {@link ParticleBuilder} instance.
     */
    public ParticleBuilder extra(double extra) {
        this.extra = extra;
        return this;
    }

    /**
     * Provides additional data required by certain particles (e.g., DustOptions).
     *
     * @param data The data object.
     * @return The current {@link ParticleBuilder} instance.
     */
    public ParticleBuilder data(@Nullable Object data) {
        this.data = data;
        return this;
    }

    /**
     * Spawns the configured particle at a specific location for everyone nearby.
     *
     * @param location The {@link Location} to spawn at.
     * @throws IllegalArgumentException If the location world is null.
     */
    public void spawn(@NotNull Location location) {
        if (location.getWorld() == null) throw new IllegalArgumentException("Location must have a world to spawn particles.");
        location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data);
    }

    /**
     * Spawns the particle at multiple locations for everyone nearby.
     *
     * @param locations A collection of {@link Location} objects.
     */
    public void spawn(@NotNull Collection<Location> locations) {
        for (Location location : locations) spawn(location);
    }

    /**
     * Spawns the particle at a specific location exclusively for one player.
     *
     * @param player   The {@link Player} who should see the particle.
     * @param location The {@link Location} to spawn at.
     */
    public void spawn(@NotNull Player player, @NotNull Location location) {
        player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data);
    }

    /**
     * Spawns the particle at multiple locations exclusively for one player.
     *
     * @param player    The {@link Player} who should see the particles.
     * @param locations A collection of {@link Location} objects.
     */
    public void spawn(@NotNull Player player, @NotNull Collection<Location> locations) {
        for (Location location : locations) spawn(player, location);
    }
}
