package me.xra1ny.pluginapi.exceptions;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import me.xra1ny.pluginapi.models.hologram.Hologram;
import org.jetbrains.annotations.NotNull;

public class HologramAlreadyRegisteredException extends RPluginException {
    public HologramAlreadyRegisteredException(@NotNull Hologram hologram) {
        super("hologram " + hologram + " is already registered!");
    }
}
