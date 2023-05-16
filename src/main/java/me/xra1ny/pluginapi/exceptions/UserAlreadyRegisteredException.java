package me.xra1ny.pluginapi.exceptions;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import me.xra1ny.pluginapi.models.user.RUser;
import org.jetbrains.annotations.NotNull;

public class UserAlreadyRegisteredException extends RPluginException {
    public UserAlreadyRegisteredException(@NotNull RUser user) {
        super("user " + user + " is already registered!");
    }
}
