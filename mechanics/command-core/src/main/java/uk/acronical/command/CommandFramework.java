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
import java.util.*;
import java.util.stream.Collectors;

/**
 * A reflection-based framework for dynamic command registration and execution.
 * <p>
 * As of version 1.0.3, this framework supports bespoke tab completion via {@link TabCompleter}
 * and command aliasing. It automates argument parsing for standard types and dispatches
 * executions to annotated methods within registered instances.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class CommandFramework {

    private final Plugin plugin;

    private final Map<String, Method> commandRegistry = new HashMap<>();
    private final Map<String, Object> commandInstances = new HashMap<>();

    private final Map<String, Method> completerRegistry = new HashMap<>();
    private final Map<String, Object> completerInstances = new HashMap<>();

    private CommandMap commandMap;

    /**
     * Initialises the framework and accesses the internal Bukkit {@link CommandMap}.
     *
     * @param plugin The {@link Plugin} instance to associate with this framework.
     * @throws RuntimeException If the internal {@link CommandMap} cannot be accessed via reflection.
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
     * Scans an object instance for {@link Command} and {@link TabCompleter} annotations.
     * <p>
     * Commands are registered both by their primary name and their defined aliases.
     * Completers are mapped to the primary command name.
     *
     * @param instance The object containing the handler methods.
     */
    public void registerCommands(@NotNull Object instance) {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command commandInfo = method.getAnnotation(Command.class);
                String mainName = commandInfo.name().toLowerCase();

                commandRegistry.put(mainName, method);
                commandInstances.put(mainName, instance);

                for (String alias : commandInfo.aliases()) {
                    commandRegistry.put(alias.toLowerCase(), method);
                    commandInstances.put(alias.toLowerCase(), instance);
                }

                BukkitCommand bukkitCommand = getBukkitCommand(commandInfo);
                commandMap.register(plugin.getName(), bukkitCommand);
            }

            if (method.isAnnotationPresent(TabCompleter.class)) {
                TabCompleter completerInfo = method.getAnnotation(TabCompleter.class);
                String targetName = completerInfo.value().toLowerCase();

                completerRegistry.put(targetName, method);
                completerInstances.put(targetName, instance);
            }
        }
    }

    /**
     * Wraps the {@link Command} metadata into a {@link BukkitCommand} for Bukkit registration.
     *
     * @param commandInfo The annotation metadata.
     * @return A {@link BukkitCommand} initialised with permissions, usage, and aliases.
     */
    @NotNull
    private BukkitCommand getBukkitCommand(@NotNull Command commandInfo) {
        BukkitCommand bukkitCommand = new BukkitCommand(commandInfo.name().toLowerCase()) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                return handleCommand(sender, commandLabel, args);
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
                return handleTabComplete(sender, alias, args);
            }
        };

        if (!commandInfo.permission().isEmpty()) {
            bukkitCommand.setPermission(commandInfo.permission());
        }

        if (!commandInfo.usage().isEmpty()) {
            bukkitCommand.setUsage(commandInfo.usage());
        }

        if (commandInfo.aliases().length > 0) {
            bukkitCommand.setAliases(Arrays.asList(commandInfo.aliases()));
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

        if (params.length == 0) return resolved;

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

    /**
     * Determines the appropriate tab completion suggestions for the current input.
     * <p>
     * Prioritises methods annotated with {@link TabCompleter}. If none exist, it falls back
     * to type-based inference for standard parameter types.
     *
     * @param sender The source of the tab completion request.
     * @param alias  The command alias being completed.
     * @param args   The current arguments entered by the user.
     * @return A filtered list of suggestions matching the current input.
     */
    @SuppressWarnings("unchecked")
    private List<String> handleTabComplete(CommandSender sender, String alias, String[] args) {
        String lowerAlias = alias.toLowerCase();
        Method commandMethod = commandRegistry.get(lowerAlias);

        if (commandMethod == null) return List.of();

        Command cmdAnnotation = commandMethod.getAnnotation(Command.class);
        String mainName = cmdAnnotation.name().toLowerCase();

        if (completerRegistry.containsKey(mainName)) {
            try {
                Method completerMethod = completerRegistry.get(mainName);
                Object instance = completerInstances.get(mainName);

                return (List<String>) completerMethod.invoke(instance, sender, args);
            } catch (Exception e) {
                LoggerUtils.severe("Failed to invoke custom tab completer for: " + mainName);
                LoggerUtils.severe(e.getMessage());
                return List.of();
            }
        }

        int argIndex = args.length - 1;
        Parameter[] params = commandMethod.getParameters();

        int paramIndex = argIndex + 1;
        if (paramIndex >= params.length) return List.of();

        Class<?> type = params[paramIndex].getType();
        String currentInput = args[argIndex].toLowerCase();

        List<String> suggestions = new ArrayList<>();

        if (type == Player.class) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
        } else if (type.isEnum()) {
            for (Object constant : type.getEnumConstants()) suggestions.add(constant.toString());
        } else if (type == boolean.class || type == Boolean.class) {
            suggestions.add("true");
            suggestions.add("false");
        } else {
            return List.of();
        }

        return suggestions.stream().filter(s -> s.toLowerCase().startsWith(currentInput)).collect(Collectors.toList());
    }
}
