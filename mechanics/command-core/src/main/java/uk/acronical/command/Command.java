package uk.acronical.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a command handler for the custom command system.
 * <p>
 * This annotation allows for the declarative definition of command metadata,
 * including permissions, usage instructions, and execution constraints.
 *
 * @author Acronical
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    /**
     * The primary label or name of the command.
     *
     * @return The command name.
     */
    String name();

    /**
     * The permission node required to execute this command.
     * <p>
     * If left as an empty string, no permission check will be performed
     * by the framework.
     *
     * @return The required permission string.
     */
    String permission() default "";

    /**
     * Determines whether the command is restricted to players.
     * <p>
     * If {@code true}, the console and other non-player senders will be
     * blocked from executing the annotated method.
     *
     * @return {@code true} if only players can use this command; otherwise {@code false}.
     */
    boolean playerOnly() default true;

    /**
     * A brief description of how to use the command correctly.
     * <p>
     * This is typically displayed to the sender when they provide
     * invalid arguments.
     *
     * @return The command usage string.
     */
    String usage() default "";
}
