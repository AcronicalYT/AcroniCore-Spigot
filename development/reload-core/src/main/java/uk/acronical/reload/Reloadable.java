package uk.acronical.reload;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a component or service that can be dynamically reloaded.
 * <p>
 * Implementing this interface allows the framework to automatically
 * trigger reload sequences for configurations, caches, or services
 * without requiring a full server restart.
 *
 * @author Acronical
 * @since 1.0.6
 */
public interface Reloadable {

    /**
     * Retrieves the friendly name of this component for logging purposes.
     *
     * @return The descriptive identifier for this reloadable module.
     */
    @NotNull
    String getReloadName();

    /**
     * Retrieves the execution priority for this component's reload sequence.
     * <p>
     * Modules with higher priorities are reloaded first to ensure
     * core dependencies (like configurations) are initialised before the
     * services that utilise them.
     *
     * @return The reload priority. Defaults to {@link ReloadPriority#NORMAL}.
     */
    @NotNull
    default ReloadPriority getPriority() {
        return ReloadPriority.NORMAL;
    }

    /**
     * Executes the reload logic for this component.
     * <p>
     * This method should clear internal caches, re-read configuration files,
     * and safely re-establish states as required.
     */
    void onReload();
}
