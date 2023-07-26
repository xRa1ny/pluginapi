package me.xra1ny.pluginapi.exceptions.hologram;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import me.xra1ny.pluginapi.models.hologram.Hologram;
import org.jetbrains.annotations.NotNull;

public class HologramNotRegisteredException extends RPluginException {
    public HologramNotRegisteredException(@NotNull Hologram hologram) {
        super("hologram " + hologram + " is not yet registered!");
    }
}
