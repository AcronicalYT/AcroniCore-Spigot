package uk.acronical.ui.input;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * A data model representing a sign-based text input request for a player.
 * <p>
 * This class facilitates the capture of up to four lines of text through a
 * native Minecraft sign interface, returning the results via a callback.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class SignInput {

    private final Player target;
    private final String[] defaultLines;
    private final BiConsumer<Player, String[]> callback;

    /**
     * Initialises a new {@link SignInput} request.
     *
     * @param target       The player who will provide the input.
     * @param defaultLines The initial text to display on the sign (max 4 lines).
     * @param callback     A {@link BiConsumer} executed upon completion, providing
     * the player and the resulting string array.
     */
    public SignInput(@NotNull Player target, @Nullable String[] defaultLines, @NotNull BiConsumer<Player, String[]> callback) {
        this.target = target;
        this.defaultLines = new String[4];
        for (int i = 0; i < 4; i++) {
            this.defaultLines[i] = (defaultLines != null && i < defaultLines.length) ? defaultLines[i] : "";
        }
        this.callback = callback;
    }

    /**
     * Retrieves the player targeted for this input.
     *
     * @return The target {@link Player}.
     */
    @NotNull
    public Player getTarget() {
        return target;
    }

    /**
     * Retrieves the default lines displayed when the sign opens.
     *
     * @return An array of exactly four strings.
     */
    @NotNull
    public String[] getDefaultLines() {
        return defaultLines;
    }

    /**
     * Finalises the input process and triggers the associated callback.
     *
     * @param results The four lines of text captured from the sign.
     */
    public void complete(@NotNull String[] results) {
        callback.accept(target, results);
    }
}
