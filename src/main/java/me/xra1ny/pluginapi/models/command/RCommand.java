package me.xra1ny.pluginapi.models.command;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import me.xra1ny.pluginapi.models.user.RUser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/** Used to create Commands */
@Slf4j
public abstract class RCommand implements CommandExecutor, TabExecutor {
    /**
     * the name of this command
     */
    @Getter(onMethod = @__(@NotNull))
    private final String name;

    /**
     * the permission of this command
     */
    @Getter(onMethod = @__(@NotNull))
    private final String permission;

    /**
     * the valid arguments of this command
     */
    @Getter(onMethod = @__(@NotNull))
    private final CommandArg[] args;

    @Getter(onMethod = @__(@NotNull))
    private final boolean requiresPlayer;

    @Getter
    private final boolean localised;

    public RCommand() throws ClassNotAnnotatedException {
        final CommandInfo info = getClass().getDeclaredAnnotation(CommandInfo.class);

        if(info == null) {
            throw new ClassNotAnnotatedException(getClass(), CommandInfo.class);
        }

        this.name = info.name();
        this.permission = info.permission();
        this.args = info.args();
        this.requiresPlayer = info.requiresPlayer();
        this.localised = info.localised();
    }

    /**
     * called when this command is executed with only the base command (/commandname)
     * @param sender the sender
     * @return the status of this command execution
     */
    @NotNull
    protected abstract CommandReturnState executeBaseCommand(@NotNull CommandSender sender);

    /**
     * Returns the Help Screen for this Command, excluding the Plugin Prefix
     */
    @NotNull
    @Unmodifiable
    protected abstract List<String> help(@NotNull CommandSender sender);

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!this.permission.isBlank() && !sender.hasPermission(this.permission)) {
            RPlugin.sendMessage(sender, RPlugin.getInstance().getPlayerNoPermissionErrorMessage());

            return true;
        }

        if(this.requiresPlayer) {
            if(!(sender instanceof Player)) {
                RPlugin.sendMessage(sender, RPlugin.getInstance().getCommandOnlyPlayerErrorMessage());

                return true;
            }
        }

        RUser user = null;

        if(sender instanceof Player) {
            user = RPlugin.getInstance().getUserManager().get((Player) sender);
        }

        try {
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("help")) {
                    sendHelpScreen(sender);

                    return true;
                }
            }

            final StringBuilder formattedArgsBuilder = new StringBuilder();
            final List<String> values = new ArrayList<>();

            for(String arg : args) {
                boolean contains = false;

                for(CommandArg commandArg : this.args) {
                    final String[] splitCommandArg = commandArg.value().split(" ");
                    boolean containsArg = false;

                    for(String singleCommandArg : splitCommandArg) {
                        if(singleCommandArg.equalsIgnoreCase(arg)) {
                            containsArg = true;

                            break;
                        }
                    }

                    if(containsArg) {
                        contains = true;

                        break;
                    }
                }

                if(contains) {
                    formattedArgsBuilder.append(!formattedArgsBuilder.isEmpty() ? " " : "").append(arg);

                    continue;
                }

                formattedArgsBuilder.append(!formattedArgsBuilder.isEmpty() ? " " : "").append("?");
                values.add(arg);
            }

            CommandArg finalCommandArg = null;

            for(CommandArg commandArg : this.args) {
                if(commandArg.value().replaceAll("%[A-Za-z0-9]*%", "?").equalsIgnoreCase(formattedArgsBuilder.toString())) {
                    finalCommandArg = commandArg;

                    break;
                }
            }

            CommandReturnState commandReturnState = null;

            if(finalCommandArg != null) {
                commandReturnState = executeCommandArgHandlerMethod(sender, finalCommandArg, values.toArray(new String[0]));
            }else {
                for(CommandArg commandArg : this.args) {
                    if(!formattedArgsBuilder.toString().startsWith(commandArg.value().replaceAll("%[A-Za-z0-9]*%", "?").replace("*", ""))) {
                        continue;
                    }

                    commandReturnState = executeCommandArgHandlerMethod(sender, commandArg, values.toArray(new String[0]));

                    break;
                }
            }

            if(finalCommandArg == null) {
                commandReturnState = executeBaseCommand(sender);
            }

            if(commandReturnState == CommandReturnState.ERROR) {
                if(sender instanceof Player) {
                    RPlugin.sendMessage(sender, (this.localised ? RPlugin.getInstance().getLocalisationManager().get(user.getLocalisationConfigName(), RPlugin.getInstance().getCommandErrorMessage()) : RPlugin.getInstance().getCommandErrorMessage()));
                }else {
                    RPlugin.sendMessage(sender, RPlugin.getInstance().getCommandErrorMessage());
                }
            }else if(commandReturnState == CommandReturnState.INVALID_ARGS) {
                if(sender instanceof Player) {
                    RPlugin.sendMessage(sender, (this.localised ? RPlugin.getInstance().getLocalisationManager().get(user.getLocalisationConfigName(), RPlugin.getInstance().getCommandInvalidArgsErrorMessage()) : RPlugin.getInstance().getCommandInvalidArgsErrorMessage()));
                }else {
                    RPlugin.sendMessage(sender, RPlugin.getInstance().getCommandInvalidArgsErrorMessage());
                }
            }

            return true;
        }catch(Exception ex) {
            if(sender instanceof Player) {
                RPlugin.sendMessage(sender, (this.localised ? RPlugin.getInstance().getLocalisationManager().get(user.getLocalisationConfigName(), RPlugin.getInstance().getCommandInternalErrorMessage()) : RPlugin.getInstance().getCommandInternalErrorMessage()));
            }else {
                RPlugin.sendMessage(sender, RPlugin.getInstance().getCommandInternalErrorMessage());
            }

            sender.sendMessage(ChatColor.RED.toString() + ex);
        }

        return true;
    }

    @NotNull
    private CommandReturnState executeCommandArgHandlerMethod(@NotNull CommandSender sender, @NotNull CommandArg commandArg, @NotNull String @NotNull [] values) throws InvocationTargetException, IllegalAccessException {
        Method commandArgHandlerMethod = null;

        for(Method method : getClass().getMethods()) {
            final CommandArgHandler commandArgHandler = method.getDeclaredAnnotation(CommandArgHandler.class);

            if(commandArgHandler == null || !List.of(commandArgHandler.value()).contains(commandArg.value())) {
                continue;
            }

            commandArgHandlerMethod = method;

            break;
        }

        if(commandArgHandlerMethod == null) {
            return CommandReturnState.INVALID_ARGS;
        }

        return (CommandReturnState) commandArgHandlerMethod.invoke(this, sender, commandArg, values);
    }

    @NotNull
    public abstract List<String> onCommandTabComplete(@NotNull CommandSender sender, @NotNull String args);

    @NotNull
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        final List<String> tabCompleted = new ArrayList<>();

        if(args.length == 1) {
            tabCompleted.add("help");
        }

        for(CommandArg arg : this.args) {
            final String[] originalArgs = arg.value().split(" ");
            final String[] editedArgs = originalArgs.clone();

            if(originalArgs.length >= args.length || originalArgs[originalArgs.length-1].endsWith("%*")) {
                for(int i = 0; i < args.length; i++) {
                    final String originalArg = i >= originalArgs.length ? originalArgs[originalArgs.length-1] : originalArgs[i];

                    if (!originalArg.startsWith("%") && !(originalArg.endsWith("%") || originalArg.endsWith("%*"))) {
                        continue;
                    }

                    editedArgs[i >= editedArgs.length ? editedArgs.length-1 : i] = args[i];
                }

                final String finalArg = originalArgs[args.length-1 >= originalArgs.length ? originalArgs.length-1 : args.length-1];

                if(!String.join(" ", editedArgs).startsWith(String.join(" ", args))) {
                    continue;
                }

                if(finalArg.startsWith("%") && finalArg.endsWith("%*")) {
                    tabCompleted.add(finalArg.replace("%", "").replace("%*", ""));

                    break;
                }

                if(finalArg.equalsIgnoreCase(CommandArg.PLAYER)) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(tabCompleted.contains(player.getName())) {
                            continue;
                        }

                        tabCompleted.add(player.getName());
                    }
                }else if(finalArg.startsWith(CommandArg.NUMBER)) {
                    tabCompleted.add("0");
                }else if(finalArg.startsWith(CommandArg.BOOLEAN)) {
                    tabCompleted.add("true");
                    tabCompleted.add("false");
                }else if(finalArg.startsWith("%") && (finalArg.endsWith("%") || finalArg.endsWith("%*"))) {
                    tabCompleted.add(finalArg.replace("%", "").replace("%*", ""));
                }else {
                    tabCompleted.add(finalArg);
                }
            }
        }

        final List<String> commandArgs = new ArrayList<>(Stream.of(this.args)
                .map(CommandArg::value)
                .map(String::toLowerCase)
                .toList());
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
            }
        }

        tabCompleted.addAll(onCommandTabComplete(sender, builder.toString()));

        return tabCompleted;
    }

    private void sendHelpScreen(@NotNull CommandSender sender) {
        for(String line : help(sender)) {
            final String finalLine = (sender instanceof Player ? this.localised ? RPlugin.getInstance().getLocalisationManager().get(RPlugin.getInstance().getUserManager().get((Player) sender).getLocalisationConfigName(), line) : line : line);

            sender.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + finalLine);
        }
    }
}
