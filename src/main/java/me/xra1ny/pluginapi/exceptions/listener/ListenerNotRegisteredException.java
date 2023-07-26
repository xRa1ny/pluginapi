package me.xra1ny.pluginapi.exceptions.listener;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ListenerNotRegisteredException extends RPluginException {
    public ListenerNotRegisteredException(@NotNull Listener listener) {
        super("listener " + listener + " is not yet registered!");
    }
}
