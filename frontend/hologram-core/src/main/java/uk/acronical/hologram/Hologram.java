package uk.acronical.hologram;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import uk.acronical.common.StringUtils;

import java.util.List;

/**
 * A lightweight wrapper for managing floating text using the Minecraft {@link TextDisplay} entity.
 * <p>
 * This utilises the native Display entity system, providing high-performance
 * billboarding, scaling, and transparency without the overhead of armour stands.
 *
 * @author Acronical
 * @since 1.0.2
 */
public class Hologram {

    private final TextDisplay textDisplay;

    /**
     * Initialises a new {@link Hologram} at the specified location.
     *
     * @param location The world coordinates where the hologram will appear.
     * @throws IllegalArgumentException If the location world is null.
     */
    public Hologram(@NotNull Location location) {
        if (location.getWorld() == null) throw new IllegalArgumentException("Location must have a world");

        this.textDisplay = (TextDisplay) location.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY);

        this.textDisplay.setBillboard(Display.Billboard.CENTER);
        this.textDisplay.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        this.textDisplay.setShadowed(true);
    }

    /**
     * Updates the text displayed by the hologram.
     *
     * @param lines A list of strings to be displayed as separate lines.
     * @return The current {@link Hologram} instance.
     */
    public Hologram setLines(@NotNull List<String> lines) {
        this.textDisplay.setText(StringUtils.colour(String.join("\n", lines)));
        return this;
    }

    /**
     * Updates the text displayed by the hologram using varargs.
     *
     * @param lines The lines of text to display.
     * @return The current {@link Hologram} instance.
     */
    public Hologram setLines(@NotNull String... lines) {
        this.textDisplay.setText(StringUtils.colour(String.join("\n", lines)));
        return this;
    }

    /**
     * Adjusts the size of the hologram.
     *
     * @param scale The multiplier for the hologram's size (1.0 is default).
     * @return The current {@link Hologram} instance.
     */
    public Hologram setScale(float scale) {
        this.textDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(scale, scale, scale), new AxisAngle4f()));
        return this;
    }

    /**
     * Toggles whether the hologram is visible through solid blocks.
     *
     * @param seeThrough {@code true} to enable X-ray visibility.
     * @return The current {@link Hologram} instance.
     */
    public Hologram setSeeThrough(boolean seeThrough) {
        this.textDisplay.setSeeThrough(seeThrough);
        return this;
    }

    /**
     * Toggles the drop shadow effect on the text.
     *
     * @param shadowed {@code true} to enable text shadows.
     * @return The current {@link Hologram} instance.
     */
    public Hologram setShadowed(boolean shadowed) {
        this.textDisplay.setShadowed(shadowed);
        return this;
    }

    /**
     * Safely removes the hologram entity from the world.
     */
    public void remove() {
        if (this.textDisplay != null && !this.textDisplay.isDead()) {
            this.textDisplay.remove();
        }
    }

    /**
     * Retrieves the underlying Bukkit {@link TextDisplay} entity.
     *
     * @return The text display instance.
     */
    @NotNull
    public TextDisplay getTextDisplay() {
        return this.textDisplay;
    }
}
