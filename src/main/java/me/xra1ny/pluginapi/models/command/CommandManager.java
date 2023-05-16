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

public class CommandManager {
    @Getter(onMethod = @__(@NotNull))
    private final List<RCommand> commands = new ArrayList<>();

    public boolean isRegistered(@NotNull RCommand command) {
        return this.commands.contains(command);
    }

    public void register(@NotNull RCommand command) throws CommandAlreadyRegisteredException {
        if(isRegistered(command)) {
            throw new CommandAlreadyRegisteredException(command);
        }

        RPlugin.getInstance().getServer().getPluginCommand(command.getName()).setExecutor(command);
        this.commands.add(command);
    }

    public void registerAll(@NotNull String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CommandAlreadyRegisteredException {
        for(Class<? extends RCommand> commandClass : new Reflections(packageName).getSubTypesOf(RCommand.class)) {
            register(commandClass.getDeclaredConstructor().newInstance());
        }
    }

    public void unregister(@NotNull RCommand command) throws CommandNotRegisteredException {
        if(!isRegistered(command)) {
            throw new CommandNotRegisteredException(command);
        }

        this.commands.remove(command);
    }

    public void unregisterAll(@NotNull String packageName) throws CommandNotRegisteredException {
        for(RCommand command : this.commands) {
            if(!command.getClass().getPackage().getName().equals(packageName)) {
                continue;
            }

            unregister(command);
        }
    }

    public void unregisterAll() throws CommandNotRegisteredException {
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
