package me.xra1ny.pluginapi.models.command;

import lombok.Getter;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.CommandAlreadyRegisteredException;
import me.xra1ny.pluginapi.exceptions.CommandNotRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class CommandManager {
    @Getter(onMethod = @__(@NotNull))
    private final List<RCommand> commands = new ArrayList<>();

    public boolean isRegistered(@NotNull RCommand command) {
        return this.commands.contains(command);
    }

    public void register(@NotNull RCommand command) throws CommandAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register command " + command + "...");

        if(isRegistered(command)) {
            throw new CommandAlreadyRegisteredException(command);
        }

        RPlugin.getInstance().getServer().getPluginCommand(command.getName()).setExecutor(command);
        this.commands.add(command);

        RPlugin.getInstance().getLogger().log(Level.INFO, "command " + command + " successfully registered!");
    }

    public void registerAll(@NotNull String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CommandAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register all commands in package " + packageName + "...");

        for(Class<? extends RCommand> commandClass : new Reflections(packageName).getSubTypesOf(RCommand.class)) {
            register(commandClass.getDeclaredConstructor().newInstance());
        }
    }

    public void unregister(@NotNull RCommand command) throws CommandNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister command " + command + "...");

        if(!isRegistered(command)) {
            throw new CommandNotRegisteredException(command);
        }

        this.commands.remove(command);

        RPlugin.getInstance().getLogger().log(Level.INFO, "command " + command + " successfully unregistered!");
    }

    public void unregisterAll(@NotNull String packageName) throws CommandNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all commands in package " + packageName + "...");

        for(RCommand command : this.commands) {
            if(!command.getClass().getPackage().getName().equals(packageName)) {
                continue;
            }

            unregister(command);
        }
    }

    public void unregisterAll() throws CommandNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all commands...");

        for(RCommand command : this.commands) {
            unregister(command);
        }
    }

    @NotNull
    public List<RCommand> getAll(@NotNull String commandName) {
        return this.commands.stream()
                .filter(command -> command.getName().equals(commandName))
                .toList();
    }
}
