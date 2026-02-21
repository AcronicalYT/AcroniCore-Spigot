package uk.acronical.locale;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.acronical.common.LoggerUtils;
import uk.acronical.common.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages plugin-wide translations and language fallback logic.
 * <p>
 * This service loads YAML files from a specified directory and provides
 * colour-formatted messages based on a player's locale or a global default.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class LocaleManager {

    private final Map<String, Map<String, String>> translations = new HashMap<>();
    private final String defaultLocale;

    /**
     * Initialises the {@link LocaleManager}.
     *
     * @param defaultLocale The fallback language code (e.g., {@code "en_gb"}).
     */
    public LocaleManager(@NotNull String defaultLocale) {
        this.defaultLocale = defaultLocale.toLowerCase();
    }

    /**
     * Scans a directory for {@code .yml} files and loads them into the translation cache.
     * <p>
     * The file name (excluding the extension) is utilised as the locale identifier.
     *
     * @param directory The folder containing translation files.
     * @throws RuntimeException If the directory cannot be created.
     * @throws IllegalArgumentException If the provided path is not a directory.
     */
    public void loadLocaleDirectory(@NotNull File directory) {
        if (!directory.exists() && !directory.mkdirs()) throw new RuntimeException("Failed to create locale directory: " + directory.getAbsolutePath());
        if (!directory.isDirectory()) throw new IllegalArgumentException("Provided path is not a directory: " + directory.getAbsolutePath());

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (files == null) return;

        translations.clear();

        for (File file : files) {
            String localeName = file.getName().substring(0, file.getName().length() - 4).toLowerCase();
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

            Map<String, String> localeTranslations = new HashMap<>();

            for (String key : configuration.getKeys(true)) {
                if (configuration.isString(key)) {
                    localeTranslations.put(key, StringUtils.colour(configuration.getString(key)));
                }
            }

            translations.put(localeName, localeTranslations);
            LoggerUtils.info("Loaded locale: " + localeName + " with " + localeTranslations.size() + " translations.");
        }
    }

    /**
     * Retrieves a formatted message for a specific locale.
     * <p>
     * If the requested locale is missing the key, the service falls back to
     * the default locale before returning a "missing key" warning.
     *
     * @param playerLocale The preferred locale of the player (e.g., {@link org.bukkit.entity.Player#getLocale()}).
     * @param key          The unique translation key.
     * @return A colour-formatted message string.
     */
    @NotNull
    public String getMessage(@Nullable String playerLocale, @NotNull String key) {
        String safeLocale = playerLocale != null ? playerLocale.toLowerCase() : defaultLocale;

        if (translations.containsKey(safeLocale)) {
            String message = translations.get(safeLocale).get(key);
            if (message != null) return message;
        }

        if (translations.containsKey(defaultLocale)) {
            String message = translations.get(defaultLocale).get(key);
            if (message != null) return message;
        }

        LoggerUtils.warn("Missing translation for key '" + key + "' in locale '" + safeLocale + "' and default locale '" + defaultLocale + "'.");
        return StringUtils.colour("&cMissing translation for key: " + key);
    }
}
