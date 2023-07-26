package me.xra1ny.pluginapi.exceptions.user;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import me.xra1ny.pluginapi.models.user.UserInputWindow;
import org.jetbrains.annotations.NotNull;

public class UserInputWindowAlreadyRegisteredException extends RPluginException {
    public UserInputWindowAlreadyRegisteredException(@NotNull UserInputWindow userInputWindow) {
        super("user input window " + userInputWindow + " is already registered!");
    }
}
