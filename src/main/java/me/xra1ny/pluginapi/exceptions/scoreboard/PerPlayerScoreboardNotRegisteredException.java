package me.xra1ny.pluginapi.exceptions.scoreboard;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import me.xra1ny.pluginapi.models.scoreboard.PerPlayerScoreboard;
import org.jetbrains.annotations.NotNull;

public class PerPlayerScoreboardNotRegisteredException extends RPluginException {
    public PerPlayerScoreboardNotRegisteredException(@NotNull PerPlayerScoreboard perPlayerScoreboard) {
        super("per player scoreboard " + perPlayerScoreboard + " is not yet registered!");
    }
}
