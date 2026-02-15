package uk.acronical.common;

import net.md_5.bungee.api.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    /**
     * Colors a message by translating colour codes and hex codes.
     *
     * @param message The message to colour.
     * @return The coloured message.
     */
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
     * Beautifies a string by replacing underscores with spaces and capitalizing each word.
     *
     * @param text The text to beautify.
     * @return The beautified text.
     */
    public static String beautify(String text) {
        if (text == null) return "";
        return Stream.of(text.toLowerCase().split("_")).map(word -> word.substring(0, 1).toUpperCase() + word.substring(1)).collect(Collectors.joining(" "));
    }

    /**
     * Beautifies an enum value by replacing underscores with spaces and capitalizing each word.
     *
     * @param enumValue The enum value to beautify.
     * @return The beautified enum value.
     */
    public static String beautify(Enum<?> enumValue) {
        return beautify(enumValue.name());
    }

    /**
     * Generates a progress bar string.
     *
     * @param current The current progress value.
     * @param max The maximum progress value.
     * @param totalBars The total number of bars in the progress bar.
     * @param symbol The symbol used for the progress bar.
     * @param completedColour The colour for completed progress.
     * @param notCompletedColour The colour for not completed progress.
     * @return The progress bar string.
     */
    public static String getProgressBar(int current, int max, int totalBars, String symbol, String completedColour, String notCompletedColour) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return colour(completedColour + symbol.repeat(progressBars) + notCompletedColour + symbol.repeat(totalBars - progressBars));
    }

    /**
     * Centers the given text within a fixed width.
     *
     * @param text The text to center.
     * @return The centered text.
     */
    public static String centerText(String text) {
        int maxWidth = 80;
        int spaces = (int) Math.round((maxWidth - 1.4 * text.length()) / 2);
        return " ".repeat(Math.max(0, spaces)) + text;
    }
}