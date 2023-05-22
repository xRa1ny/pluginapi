package me.xra1ny.pluginapi.models.scoreboard;

import lombok.Getter;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.GlobalScoreboardAlreadyRegisteredException;
import me.xra1ny.pluginapi.exceptions.GlobalScoreboardNotYetRegisteredException;
import me.xra1ny.pluginapi.exceptions.PerPlayerScoreboardAlreadyRegisteredException;
import me.xra1ny.pluginapi.exceptions.PerPlayerScoreboardNotRegisteredException;
import me.xra1ny.pluginapi.models.user.RUser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ScoreboardManager {
    @Getter(onMethod = @__(@NotNull))
    private final List<GlobalScoreboard> globalScoreboards = new ArrayList<>();

    @Getter(onMethod = @__(@NotNull))
    private final List<PerPlayerScoreboard>  perPlayerScoreboards = new ArrayList<>();

    public boolean isRegistered(@NotNull GlobalScoreboard globalScoreboard) {
        return this.globalScoreboards.contains(globalScoreboard);
    }

    public void register(@NotNull GlobalScoreboard globalScoreboard) throws GlobalScoreboardAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register global scoreboard " + globalScoreboard + "...");

        if(isRegistered(globalScoreboard)) {
            throw new GlobalScoreboardAlreadyRegisteredException(globalScoreboard);
        }

        this.globalScoreboards.add(globalScoreboard);

        RPlugin.getInstance().getLogger().log(Level.INFO, "global scoreboard " + globalScoreboard + " successfully registered!");
    }

    public void unregister(@NotNull GlobalScoreboard globalScoreboard) throws GlobalScoreboardNotYetRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister global scoreboard " + globalScoreboard + "...");

        if(!isRegistered(globalScoreboard)) {
            throw new GlobalScoreboardNotYetRegisteredException(globalScoreboard);
        }

        this.globalScoreboards.remove(globalScoreboard);

        RPlugin.getInstance().getLogger().log(Level.INFO, "global scoreboard " + globalScoreboard + " successfully unregistered!");
    }

    @NotNull
    public List<GlobalScoreboard> getGlobalScoreboards(@NotNull RUser user) {
        return this.globalScoreboards.stream()
                .filter(globalScoreboard -> globalScoreboard.getUsers().contains(user))
                .toList();
    }

    public boolean isRegistered(@NotNull PerPlayerScoreboard perPlayerScoreboard) {
        return this.perPlayerScoreboards.contains(perPlayerScoreboard);
    }

    public void register(@NotNull PerPlayerScoreboard perPlayerScoreboard) throws PerPlayerScoreboardAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register per player scoreboard " + perPlayerScoreboard + "...");

        if(isRegistered(perPlayerScoreboard)) {
            throw new PerPlayerScoreboardAlreadyRegisteredException(perPlayerScoreboard);
        }

        this.perPlayerScoreboards.add(perPlayerScoreboard);

        RPlugin.getInstance().getLogger().log(Level.INFO, "per player scoreboard " + perPlayerScoreboard + " successfully registered!");
    }

    public void unregister(@NotNull PerPlayerScoreboard perPlayerScoreboard) throws PerPlayerScoreboardNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister per player scoreboard " + perPlayerScoreboard + "...");

        if(!isRegistered(perPlayerScoreboard)) {
            throw new PerPlayerScoreboardNotRegisteredException(perPlayerScoreboard);
        }

        this.perPlayerScoreboards.remove(perPlayerScoreboard);

        RPlugin.getInstance().getLogger().log(Level.INFO, "per player scoreboard " + perPlayerScoreboard + " successfully unregistered!");
    }

    @NotNull
    public List<PerPlayerScoreboard> getPerPlayerScoreboards(@NotNull RUser user) {
        return this.perPlayerScoreboards.stream()
                .filter(perPlayerScoreboard -> perPlayerScoreboard.getScoreboards().containsKey(user))
                .toList();
    }
}
