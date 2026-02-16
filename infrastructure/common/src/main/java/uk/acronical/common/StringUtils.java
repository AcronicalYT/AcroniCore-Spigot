package uk.acronical.common;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A collection of utility methods for string manipulation and formatting.
 * <p>
 * This class provides tools for colour translation (including Hex support),
 * text beautification for Enums, and visual components like progress bars.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class StringUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    /**
     * Colours a message by translating legacy colour codes and Hexadecimal codes.
     * <p>
     * Supports legacy codes using the {@code &} symbol and Hex codes in the
     * {@code &#RRGGBB} format.
     *
     * @param message The raw string to translate.
     * @return The formatted string, or an empty string if the input is null.
     */
    @NotNull
    public static String colour(String message) {
        if (message == null || message.isEmpty()) return "";

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    /**
     * Beautifies a string by replacing underscores with spaces and capitalising each word.
     *
     * @param text The raw text to process (e.g., {@code "GOLD_INGOT"}).
     * @return The formatted text (e.g., {@code "Gold Ingot"}).
     */
    @NotNull
    public static String beautify(String text) {
        if (text == null) return "";
        return Stream.of(text.toLowerCase().split("_")).map(word -> word.substring(0, 1).toUpperCase() + word.substring(1)).collect(Collectors.joining(" "));
    }

    /**
     * Beautifies an {@link Enum} value by formatting its name.
     *
     * @param enumValue The enum constant to process.
     * @return The capitalised and formatted name of the enum.
     */
    @NotNull
    public static String beautify(@NotNull Enum<?> enumValue) {
        return beautify(enumValue.name());
    }

    /**
     * Generates a visual progress bar string.
     *
     * @param current            The current progress value.
     * @param max                The maximum progress value.
     * @param totalBars          The total number of characters in the bar.
     * @param symbol             The character to use for the bar (e.g., {@code "|"}).
     * @param completedColour    The colour code for the completed portion.
     * @param notCompletedColour The colour code for the remaining portion.
     * @return A colour-translated progress bar string.
     */
    @NotNull
    public static String getProgressBar(int current, int max, int totalBars, @NotNull String symbol, @NotNull String completedColour, @NotNull String notCompletedColour) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return colour(completedColour + symbol.repeat(progressBars) + notCompletedColour + symbol.repeat(totalBars - progressBars));
    }

    /**
     * Centres text within a fixed width based on the standard Minecraft chat dimensions.
     * <p>
     * This method applies a compensation factor of {@code 1.4} to account for
     * the pixel width of the default Minecraft font.
     *
     * @param text The text to be centred.
     * @return The centred text padded with spaces.
     */
    @NotNull
    public static String centerText(String text) {
        int maxWidth = 80;
        int spaces = (int) Math.round((maxWidth - 1.4 * text.length()) / 2);
        return " ".repeat(Math.max(0, spaces)) + text;
    }
}