package me.xra1ny.pluginapi.exceptions;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import me.xra1ny.pluginapi.models.scoreboard.GlobalScoreboard;
import org.jetbrains.annotations.NotNull;

public class GlobalScoreboardNotYetRegisteredException extends RPluginException {
    public GlobalScoreboardNotYetRegisteredException(@NotNull GlobalScoreboard globalScoreboard) {
        super("global scoreboard " + globalScoreboard + " is not yet registered!");
    }
}
