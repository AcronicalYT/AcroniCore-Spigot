package uk.acronical.common;

import net.md_5.bungee.api.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    /// <summary>
    /// Colors a message using '&' for standard colors and '&#RRGGBB' for hex colors.
    /// </summary>
    /// @param message The message to color.
    /// @return The colored message.
    public static String color(String message) {
        if (message == null || message.isEmpty()) return "";

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    /// <summary>
    /// Beautifies a string by replacing underscores with spaces and capitalizing each word.
    /// </summary>
    /// @param text The text to beautify.
    /// @return The beautified text.
    public static String beautify(String text) {
        if (text == null) return "";
        return Stream.of(text.toLowerCase().split("_")).map(word -> word.substring(0, 1).toUpperCase() + word.substring(1)).collect(Collectors.joining(" "));
    }

    /// <summary>
    /// Beautifies an enum value by replacing underscores with spaces and capitalizing each word.
    /// </summary>
    /// @param enumValue The enum value to beautify.
    /// @return The beautified enum value.
    public static String beautify(Enum<?> enumValue) {
        return beautify(enumValue.name());
    }

    /// <summary>
    /// Generates a progress bar string.
    /// </summary>
    /// @param current The current progress value.
    /// @param max The maximum progress value.
    /// @param totalBars The total number of bars in the progress bar.
    /// @param symbol The symbol to use for the progress bar.
    /// @param completedColor The color code for completed progress.
    /// @param notCompletedColor The color code for not completed progress.
    /// @return The generated progress bar string.
    public static String getProgressBar(int current, int max, int totalBars, String symbol, String completedColor, String notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return color(completedColor + symbol.repeat(progressBars) + notCompletedColor + symbol.repeat(totalBars - progressBars));
    }

    /// <summary>
    /// Centers text within a width of 80 characters.
    /// </summary>
    /// @param text The text to center.
    /// @return The centered text.
    public static String centerText(String text) {
        int maxWidth = 80;
        int spaces = (int) Math.round((maxWidth - 1.4 * text.length()) / 2);
        return " ".repeat(Math.max(0, spaces)) + text;
    }
}