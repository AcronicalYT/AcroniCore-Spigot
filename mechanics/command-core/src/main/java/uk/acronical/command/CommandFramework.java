package uk.acronical.command;

import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;
import uk.acronical.common.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * A reflection-based framework for dynamic command registration and execution.
 * <p>
 * This class injects itself into the internal Bukkit {@link CommandMap} to register
 * methods annotated with {@link Command}. It handles automatic argument parsing
 * for common types like {@link Player}, {@code int}, and {@code boolean}.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class CommandFramework {

    private final Plugin plugin;
    private final Map<String, Method> commandRegistry = new HashMap<>();
    private final Map<String, Object> commandInstances = new HashMap<>();
    private CommandMap commandMap;

    /**
     * Initialises the framework and accesses the internal Bukkit command map.
     * <p>
     * Note: This uses reflection to access private fields within the server
     * implementation and may require updates if the server software changes
     * its internal structure.
     *
     * @param plugin The {@link Plugin} instance to associate with this framework.
     * @throws RuntimeException If the internal {@link CommandMap} cannot be accessed.
     */
    public CommandFramework(@NotNull Plugin plugin) {
        this.plugin = plugin;

        try {
            Field bukkitCommandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMapField.setAccessible(true);
            this.commandMap = (CommandMap) bukkitCommandMapField.get(plugin.getServer());
        } catch (Exception e) {
            LoggerUtils.severe("Could not retrieve the internal CommandMap. Dynamic command registration will fail.");
            throw new RuntimeException("Failed to initialise CommandFramework", e);
        }
    }

    /**
     * Scans an object instance for {@link Command} annotations and registers them.
     *
     * @param instance The object containing the command handler methods.
     */
    public void registerCommands(@NotNull Object instance) {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command commandInfo = method.getAnnotation(Command.class);

                commandRegistry.put(commandInfo.name().toLowerCase(), method);
                commandInstances.put(commandInfo.name().toLowerCase(), instance);

                BukkitCommand bukkitCommand = getBukkitCommand(commandInfo);
                commandMap.register(plugin.getName(), bukkitCommand);
            }
        }
    }

    /**
     * Wraps the {@link Command} metadata into a {@link BukkitCommand} for registration.
     *
     * @param commandInfo The annotation metadata.
     * @return A {@link BukkitCommand} ready for the internal map.
     */
    @NotNull
    private BukkitCommand getBukkitCommand(@NotNull Command commandInfo) {
        BukkitCommand bukkitCommand = new BukkitCommand(commandInfo.name().toLowerCase()) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                return handleCommand(sender, commandLabel, args);
            }
        };

        if (!commandInfo.permission().isEmpty()) {
            bukkitCommand.setPermission(commandInfo.permission());
        }

        if (!commandInfo.usage().isEmpty()) {
            bukkitCommand.setUsage(commandInfo.usage());
        }

        return bukkitCommand;
    }

    /**
     * Dispatches the executed command to the registered method.
     * <p>
     * This method performs permission checks and requirement validation (such as
     * {@code playerOnly}) before attempting to resolve method arguments.
     *
     * @param sender       The source of the command.
     * @param commandLabel The alias or name used.
     * @param args         The raw arguments provided by the sender.
     * @return {@code true} to satisfy the Bukkit executor contract.
     */
    private boolean handleCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Method method = commandRegistry.get(commandLabel.toLowerCase());
        Object instance = commandInstances.get(commandLabel.toLowerCase());

        if (method == null || instance == null) {
            sender.sendMessage("Unknown command: " + commandLabel);
            return true;
        }

        Command command = method.getAnnotation(Command.class);

        if (command.playerOnly() && !(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        if (!command.permission().isEmpty() && !sender.hasPermission(command.permission())) {
            sender.sendMessage("You do not have permission to execute this command.");
            return true;
        }

        try {
            Object[] invokedArgs = resolveArguments(method, sender, args);
            method.invoke(instance, invokedArgs);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(StringUtils.colour("&cUsage Error: " + e.getMessage()));
            if (!command.usage().isEmpty()) sender.sendMessage(StringUtils.colour("&cUsage: " + command.usage()));
        } catch (InvocationTargetException e) {
            sender.sendMessage(StringUtils.colour("&cAn internal error occurred while executing this command."));
            LoggerUtils.severe("Exception in command '" + commandLabel + "':");
            if (e.getCause() != null) LoggerUtils.severe(e.getCause().getMessage());
        } catch (Exception e) {
            sender.sendMessage(StringUtils.colour("&cAn error occurred loading the command."));
            LoggerUtils.severe(e.getMessage());
        }

        return true;
    }

    /**
     * Maps raw command arguments to the parameter types of the handler method.
     * <p>
     * Supports automatic conversion for:
     * <ul>
     * <li>{@link Player} (via name lookup)</li>
     * <li>Integers and Doubles</li>
     * <li>Booleans</li>
     * <li>Strings</li>
     * </ul>
     *
     * @param method The method to inspect.
     * @param sender The command executor.
     * @param args   The raw string arguments.
     * @return An array of resolved objects for reflection invocation.
     * @throws IllegalArgumentException If argument counts mismatch or types are invalid.
     */
    private Object[] resolveArguments(Method method, CommandSender sender, String[] args) {
        Parameter[] params = method.getParameters();
        Object[] resolved = new Object[params.length];

        if (params.length == 0) {
            return resolved;
        }

        Class<?> senderType = params[0].getType();

        if (Player.class.isAssignableFrom(senderType) && !(sender instanceof Player)) {
            throw new IllegalArgumentException("This command requires a Player executor.");
        }

        if (org.bukkit.command.ConsoleCommandSender.class.isAssignableFrom(senderType) && !(sender instanceof org.bukkit.command.ConsoleCommandSender)) {
            throw new IllegalArgumentException("This command requires a Console executor.");
        }

        resolved[0] = sender;

        for (int i = 1; i < params.length; i++) {
            int argIndex = i - 1;

            if (argIndex >= args.length) {
                throw new IllegalArgumentException("Not enough arguments!");
            }

            Class<?> type = params[i].getType();
            String input = args[argIndex];

            if (type == int.class || type == Integer.class) {
                try { resolved[i] = Integer.parseInt(input); }
                catch (NumberFormatException e) { throw new IllegalArgumentException(input + " is not a valid integer."); }
            } else if (type == double.class || type == Double.class) {
                try { resolved[i] = Double.parseDouble(input); }
                catch (NumberFormatException e) { throw new IllegalArgumentException(input + " is not a valid number."); }
            } else if (type == boolean.class || type == Boolean.class) {
                if (input.equalsIgnoreCase("true")) resolved[i] = true;
                else if (input.equalsIgnoreCase("false")) resolved[i] = false;
                else throw new IllegalArgumentException(input + " is not a valid boolean (true/false).");
            } else if (type == Player.class) {
                Player target = plugin.getServer().getPlayer(input);
                if (target == null) throw new IllegalArgumentException("Player " + input + " not found.");
                resolved[i] = target;
            } else {
                resolved[i] = input;
            }
        }
        return resolved;
    }
}
