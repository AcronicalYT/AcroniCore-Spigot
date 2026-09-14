package uk.acronical.nms;

import org.bukkit.Bukkit;
import uk.acronical.common.LoggerUtils;

import java.lang.reflect.Field;

/**
 * A utility factory for retrieving server performance metrics.
 * <p>
 * This class attempts to use native Spigot/Paper APIs first for optimal
 * performance. If unavailable, it falls back to a cached reflection
 * wrapper around the NMS server.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class PerformanceFactory {

    private static final double[] FALLBACK_TPS = {20.0, 20.0, 20.0};

    private static Object nmsServer;
    private static Field tpsField;
    private static boolean useReflection = false;

    static {
        try {
            Bukkit.class.getMethod("getTPS");
        } catch (NoSuchMethodException ignored) {
            try {
                Object craftServer = ReflectionUtils.getCraftBukkitClass("CraftServer").cast(Bukkit.getServer());
                nmsServer = craftServer.getClass().getMethod("getServer").invoke(craftServer);
                tpsField = nmsServer.getClass().getField("recentTps");
                useReflection = true;
            } catch (Exception e) {
                LoggerUtils.warn("Failed to hook into NMS for TPS tracking. Metrics may be inaccurate.");
            }
        }
    }

    /**
     * Retrieves the server's recent TPS (1m, 5m, 15m averages).
     *
     * @return An array of TPS averages, or a fallback array if retrieval fails.
     */
    public static double[] getRecentTps() {
        if (!useReflection) {
            try {
                return (double[]) Bukkit.class.getMethod("getTPS").invoke(null);
            } catch (Exception ignored) {
                useReflection = true;
            }
        }

        if (tpsField != null && nmsServer != null) {
            try {
                return (double[]) tpsField.get(nmsServer);
            } catch (Exception e) {
                return FALLBACK_TPS;
            }
        }

        return FALLBACK_TPS;
    }

    /**
     * Retrieves the immediate 1-minute TPS average, formatted for display.
     *
     * @return A formatted string capped at "20.00".
     */
    public static String getFormattedTps() {
        double tps = getRecentTps()[0];
        return String.format("%.2f", Math.min(20.0, tps));
    }
}
