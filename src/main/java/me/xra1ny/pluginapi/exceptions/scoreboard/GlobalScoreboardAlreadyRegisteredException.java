package me.xra1ny.pluginapi.exceptions.scoreboard;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import me.xra1ny.pluginapi.models.scoreboard.GlobalScoreboard;
import org.jetbrains.annotations.NotNull;

public class GlobalScoreboardAlreadyRegisteredException extends RPluginException {
    public GlobalScoreboardAlreadyRegisteredException(@NotNull GlobalScoreboard globalScoreboard) {
        super("global scoreboard " + globalScoreboard + " is already registered!");
    }
}
