package me.xra1ny.pluginapi.exceptions;

import me.xra1ny.pluginapi.models.command.RCommand;
import me.xra1ny.pluginapi.models.exception.RPluginException;
import org.jetbrains.annotations.NotNull;

public class CommandAlreadyRegisteredException extends RPluginException {
    public CommandAlreadyRegisteredException(@NotNull RCommand command) {
        super("command " + command + " is already registered!");
    }
}
