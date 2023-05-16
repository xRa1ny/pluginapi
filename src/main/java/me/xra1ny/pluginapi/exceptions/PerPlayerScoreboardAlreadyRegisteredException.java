package me.xra1ny.pluginapi.exceptions;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import me.xra1ny.pluginapi.models.scoreboard.PerPlayerScoreboard;
import org.jetbrains.annotations.NotNull;

public class PerPlayerScoreboardAlreadyRegisteredException extends RPluginException {
    public PerPlayerScoreboardAlreadyRegisteredException(@NotNull PerPlayerScoreboard  perPlayerScoreboard) {
        super("per player scoreboard " + perPlayerScoreboard + " is already registered!");
    }
}
