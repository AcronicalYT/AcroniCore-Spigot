package uk.acronical.command;

import java.lang.annotation.*;

/**
 * Marks a method as a provider for tab-completion suggestions.
 * <p>
 * This annotation allows the {@link CommandFramework} to identify methods
 * responsible for generating dynamic suggestions when a player presses
 * the 'tab' key while typing a command.
 *
 * @author Acronical
 * @since 1.0.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TabCompleter {

    /**
     * The name or alias of the command this completer is associated with.
     * <p>
     * This should match the {@link Command#name()} or one of the
     * {@link Command#aliases()} defined in the framework.
     *
     * @return The target command name.
     */
    String value();
}