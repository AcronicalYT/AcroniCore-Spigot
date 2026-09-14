package uk.acronical.reload;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The central service for managing and executing framework reload cycles.
 * <p>
 * This service safely iterates through all registered {@link Reloadable} components
 * based on their priority, catching and isolating failures to ensure a single
 * broken module does not halt the entire server reload.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class ReloadService {

    private final List<Reloadable> reloadables = new CopyOnWriteArrayList<>();

    /**
     * Registers a component to be triggered during a reload cycle.
     *
     * @param reloadable The component to register.
     */
    public void register(@NotNull Reloadable reloadable) {
        if (!reloadables.contains(reloadable)) reloadables.add(reloadable);
    }

    /**
     * Removes a component from the reload cycle.
     *
     * @param reloadable The component to unregister.
     */
    public void unregister(@NotNull Reloadable reloadable) {
        reloadables.remove(reloadable);
    }

    /**
     * Executes the reload sequence across all registered components.
     *
     * @return The immutable summary of the execution results.
     */
    @NotNull
    public ReloadResult reloadAll() {
        long startNanos = System.nanoTime();
        Map<String, Throwable> failures = new LinkedHashMap<>();

        List<Reloadable> sorted = new ArrayList<>(reloadables);
        sorted.sort(Comparator.comparing(Reloadable::getPriority));

        for (Reloadable item : sorted) {
            try {
                item.onReload();
            } catch (Throwable t) {
                failures.put(item.getReloadName(), t);
                LoggerUtils.severe("Failed to reload component: " + item.getReloadName());

                String errorMessage = t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();
                LoggerUtils.severe(errorMessage);
            }
        }

        long elapsedNanos = System.nanoTime() - startNanos;
        return new ReloadResult(elapsedNanos, failures, sorted.size());
    }

    /**
     * Executes the reload sequence and dispatches a formatted report to the sender.
     *
     * @param sender The entity (player or console) initiating the reload.
     */
    public void reloadAndNotify(@NotNull CommandSender sender) {
        sender.sendMessage("§7Initiating framework reload...");
        ReloadResult result = reloadAll();

        if (result.isSuccessful()) {
            sender.sendMessage(String.format("§aReload complete. §f%d/%d §acomponents reloaded in §e%.2fms§a.", result.getSuccessCount(), result.getTotalCount(), result.getElapsedMillis()));
        } else {
            sender.sendMessage(String.format("§cReload finished with §4%d §cfailures in §e%.2fms§c.", result.getFailures().size(), result.getElapsedMillis()));
            result.getFailures().forEach((name, error) -> sender.sendMessage("§c - " + name + ": §7" + error.getMessage()));
        }
    }
}
