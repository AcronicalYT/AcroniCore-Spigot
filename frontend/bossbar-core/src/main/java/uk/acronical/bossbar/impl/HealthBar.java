package uk.acronical.bossbar.impl;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import uk.acronical.bossbar.BaseBossBar;

/**
 * A specialised {@link BaseBossBar} designed to track and visualise the health
 * of a {@link LivingEntity}.
 * <p>
 * This class facilitates the creation of "Boss" style health overlays for custom
 * mobs or competitive player scenarios, utilised by calling the {@link #update()}
 * method during damage events.
 *
 * @author Acronical
 * @since 1.0.4
 */
public class HealthBar extends BaseBossBar {

    private final LivingEntity target;

    /**
     * Initialises a new health tracking bar for a specific entity.
     *
     * @param target The entity whose health will be tracked.
     * @param title  The display name of the health bar.
     * @param colour The {@link BarColor} of the bar.
     * @param style  The {@link BarStyle} of the bar.
     */
    public HealthBar(@NotNull LivingEntity target, @NotNull String title, @NotNull BarColor colour, @NotNull BarStyle style) {
        super(title, colour, style);
        this.target = target;
        update(); // Initial sync
    }

    /**
     * Synchronises the boss bar's progress with the entity's current health.
     * <p>
     * This should typically be invoked within an EntityDamageEvent or a
     * repeating task to ensure the UI remains accurate.
     */
    public void update() {
        if (target == null || target.isDead()) {
            bossBar.setProgress(0.0);
            return;
        }

        AttributeInstance maxHealthAttr = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttr == null) return;


        double maxHealth = maxHealthAttr.getValue();
        double currentHealth = target.getHealth();

        // Ensure progress stays between 0.0 and 1.0
        double progress = currentHealth / maxHealth;
        bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
    }

    /**
     * Retrieves the entity currently being tracked by this health bar.
     *
     * @return The target {@link LivingEntity}.
     */
    @NotNull
    public LivingEntity getTarget() {
        return target;
    }
}
