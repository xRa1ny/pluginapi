package me.xra1ny.pluginapi.exceptions.user;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import me.xra1ny.pluginapi.models.user.UserInputWindow;
import org.jetbrains.annotations.NotNull;

public class UserInputWindowNotRegisteredException extends RPluginException {
    public UserInputWindowNotRegisteredException(@NotNull UserInputWindow userInputWindow) {
        super("user input window " + userInputWindow + " is not yet registered!");
    }
}
