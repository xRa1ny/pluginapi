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
    /**
     * the global scoreboards of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<GlobalScoreboard> globalScoreboards = new ArrayList<>();

    /**
     * the per player scoreboard of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<PerPlayerScoreboard>  perPlayerScoreboards = new ArrayList<>();

    /**
     * checks if the global scoreboard specified is registered or not
     * @param globalScoreboard the global scoreboard
     * @return true if the global scoreboard specified is registered, false otherwise
     */
    public boolean isRegistered(@NotNull GlobalScoreboard globalScoreboard) {
        return this.globalScoreboards.contains(globalScoreboard);
    }

    /**
     * registers the global scoreboard specified
     * @param globalScoreboard the global scoreboard
     * @throws GlobalScoreboardAlreadyRegisteredException if the global scoreboard specified is already registered
     */
    public void register(@NotNull GlobalScoreboard globalScoreboard) throws GlobalScoreboardAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register global scoreboard " + globalScoreboard + "...");

        if(isRegistered(globalScoreboard)) {
            throw new GlobalScoreboardAlreadyRegisteredException(globalScoreboard);
        }

        this.globalScoreboards.add(globalScoreboard);

        RPlugin.getInstance().getLogger().log(Level.INFO, "global scoreboard " + globalScoreboard + " successfully registered!");
    }

    /**
     * unregisters the global scoreboard specified
     * @param globalScoreboard the global scoreboard
     * @throws GlobalScoreboardNotYetRegisteredException if the global scoreboard specified is not yet registered
     */
    public void unregister(@NotNull GlobalScoreboard globalScoreboard) throws GlobalScoreboardNotYetRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister global scoreboard " + globalScoreboard + "...");

        if(!isRegistered(globalScoreboard)) {
            throw new GlobalScoreboardNotYetRegisteredException(globalScoreboard);
        }

        this.globalScoreboards.remove(globalScoreboard);

        RPlugin.getInstance().getLogger().log(Level.INFO, "global scoreboard " + globalScoreboard + " successfully unregistered!");
    }

    /**
     * retrieves all global scoreboards the user specified is part of
     * @param user the user
     * @return all global scoreboards the user specified is part of
     */
    @NotNull
    public List<GlobalScoreboard> getGlobalScoreboards(@NotNull RUser user) {
        return this.globalScoreboards.stream()
                .filter(globalScoreboard -> globalScoreboard.getUsers().contains(user))
                .toList();
    }

    /**
     * checks if the per player scoreboard specified is registered or not
     * @param perPlayerScoreboard the per player scoreboard
     * @return true is the per player scoreboard specified is registered, false otherwise
     */
    public boolean isRegistered(@NotNull PerPlayerScoreboard perPlayerScoreboard) {
        return this.perPlayerScoreboards.contains(perPlayerScoreboard);
    }

    /**
     * registers the per player scoreboard specified
     * @param perPlayerScoreboard the per player scoreboard
     * @throws PerPlayerScoreboardAlreadyRegisteredException if the per player scoreboard specified is already registered
     */
    public void register(@NotNull PerPlayerScoreboard perPlayerScoreboard) throws PerPlayerScoreboardAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register per player scoreboard " + perPlayerScoreboard + "...");

        if(isRegistered(perPlayerScoreboard)) {
            throw new PerPlayerScoreboardAlreadyRegisteredException(perPlayerScoreboard);
        }

        this.perPlayerScoreboards.add(perPlayerScoreboard);

        RPlugin.getInstance().getLogger().log(Level.INFO, "per player scoreboard " + perPlayerScoreboard + " successfully registered!");
    }

    /**
     * unregisters the per player scoreboard specified
     * @param perPlayerScoreboard the per player scoreboard
     * @throws PerPlayerScoreboardNotRegisteredException if the per player scoreboard specified is not yet registered
     */
    public void unregister(@NotNull PerPlayerScoreboard perPlayerScoreboard) throws PerPlayerScoreboardNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister per player scoreboard " + perPlayerScoreboard + "...");

        if(!isRegistered(perPlayerScoreboard)) {
            throw new PerPlayerScoreboardNotRegisteredException(perPlayerScoreboard);
        }

        this.perPlayerScoreboards.remove(perPlayerScoreboard);

        RPlugin.getInstance().getLogger().log(Level.INFO, "per player scoreboard " + perPlayerScoreboard + " successfully unregistered!");
    }

    /**
     * retrieves all per player scoreboards the user specified is part of
     * @param user the user
     * @return all per player scoreboard the user specified is part of
     */
    @NotNull
    public List<PerPlayerScoreboard> getPerPlayerScoreboards(@NotNull RUser user) {
        return this.perPlayerScoreboards.stream()
                .filter(perPlayerScoreboard -> perPlayerScoreboard.getScoreboards().containsKey(user))
                .toList();
    }
}
