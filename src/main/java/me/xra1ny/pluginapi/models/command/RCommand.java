package me.xra1ny.pluginapi.models.command;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/** Used to create Commands */
@Slf4j
public abstract class RCommand implements CommandExecutor, TabExecutor {
    @Getter(onMethod = @__(@NotNull))
    private final String name, permission;

    @Getter(onMethod = @__(@NotNull))
    private final String[] args;

    @Getter(onMethod = @__(@NotNull))
    private final boolean requiresPlayer;

    public RCommand() throws ClassNotAnnotatedException {
        @Nullable
        final CommandInfo info = getClass().getDeclaredAnnotation(CommandInfo.class);

        if(info == null) {
            throw new ClassNotAnnotatedException(getClass(), CommandInfo.class);
        }else {
            this.name = info.name();
            this.permission = info.permission();
            this.args = info.args();
            this.requiresPlayer = info.requiresPlayer();
        }
    }

    @NotNull
    protected abstract CommandReturnState executeBaseCommand(@NotNull CommandSender sender);
    @NotNull
    protected abstract CommandReturnState executeWithArgs(@NotNull CommandSender sender, @NotNull String args, @NotNull String[] values);

    /**
     * Returns the Help Screen for this Command, excluding the Plugin Prefix
     */
    @NotNull
    @Unmodifiable
    protected abstract List<String> help(@NotNull CommandSender sender);

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!this.permission.isBlank()) {
            if(!sender.hasPermission(this.permission)) {
                sender.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getPlayerNoPermissionErrorMessage());
                return true;
            }
        }

        if(this.requiresPlayer) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(RPlugin.getInstance().getOnlyPlayerCommandErrorMessage());
                return true;
            }
        }

        try {
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("help")) {
                    sendHelpScreen(sender);

                    return true;
                }
            }

            // Create List of all valid Command Arguments in all lower case
            final List<String> commandArgs = new ArrayList<>(Stream.of(this.args).map(String::toLowerCase).toList());

            final List<String> commandValues = new ArrayList<>();
            final StringBuilder builder = new StringBuilder();

            for(String arg : args) {
                boolean contains = false;

                for(String commandArg : commandArgs) {
                    if(Arrays.asList(commandArg.split(" ")).contains(arg.toLowerCase())) {
                        contains = true;
                    }
                }

                if(contains) {
                    builder.append(builder.length() > 0 ? " " : "").append(arg);
                }else {
                    builder.append(builder.length() > 0 ? " " : "").append("?");
                    commandValues.add(arg);
                }

                contains = false;
            }

            final CommandReturnState commandReturnState;

            if(builder.length() > 0) {
                commandReturnState = executeWithArgs(sender, builder.toString(), commandValues.toArray(new String[0]));
            }else {
                commandReturnState = executeBaseCommand(sender);
            }

           if(commandReturnState == CommandReturnState.ERROR) {
               sender.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getCommandErrorMessage());
            }else if(commandReturnState == CommandReturnState.INVALID_ARGS) {
               sender.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getCommandInvalidArgsErrorMessage());
            }

           return true;
        }catch(Exception e) {
            sender.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getCommandInternalErrorMessage());
            sender.sendMessage(ChatColor.RED.toString() + e);
        }

        return true;
    }

    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        final List<String> tabCompleted = new ArrayList<>();

        if(args.length == 1) {
            tabCompleted.add("help");
        }

        for(String arg : this.args) {
            final String[] splitArgs = arg.split(" ");

            if(splitArgs.length > args.length-1) {
                final String finalArg = splitArgs[args.length-1];

                if(!arg.startsWith(String.join(" ", args))) {
                    continue;
                }

                if(finalArg.equalsIgnoreCase(RPlugin.getInstance().PLAYER_IDENTIFIER)) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        tabCompleted.add(player.getName());
                    }
                }else if(finalArg.equalsIgnoreCase(RPlugin.getInstance().COMMAND_IDENTIFIER)) {
                    tabCompleted.add(this.name);
                }else if(finalArg.equalsIgnoreCase("%INTEGER%")) {
                    tabCompleted.add("<-2.147.483.647 bis 2.147.483.647>");
                }else if(finalArg.equalsIgnoreCase("%LONG%")) {
                    tabCompleted.add("<-9.223.372.036.854.775.807 bis 9.223.372.036.854.775.807>");
                }else if(finalArg.equalsIgnoreCase("%BOOLEAN%")) {
                    tabCompleted.add("<true|false>");
                }else if(finalArg.startsWith("%") && finalArg.endsWith("%")) {
                    tabCompleted.add("<" + finalArg.replaceAll("%", "") + ">");
                }else {
                    tabCompleted.add(finalArg);
                }

            }
        }

        return tabCompleted;
    }

    private void sendHelpScreen(@NotNull CommandSender sender) {
        for(String line : help(sender)) {
            sender.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + line);
        }
    }
}
