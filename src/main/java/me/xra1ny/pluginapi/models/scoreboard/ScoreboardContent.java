package me.xra1ny.pluginapi.models.scoreboard;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Slf4j
final class ScoreboardContent {
    /**
     * the title of this scoreboard content
     */
    @Getter(onMethod = @__(@NotNull))
    private String title;

    /**
     * the bukkit instance this scoreboard content represents
     */
    @Getter(onMethod = @__(@NotNull))
    private final Scoreboard bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    /**
     * the scoreboard teams of this scoreboard content
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<ScoreboardTeam> teams = new ArrayList<>();

    ScoreboardContent(@NotNull String title) {
        this.title = title;
    }

    /**
     * updates this scoreboard content
     */
    public void update() {
        Objective objective = this.bukkitScoreboard.getObjective(ChatColor.stripColor(this.title));

        if(objective == null) {
            objective = this.bukkitScoreboard.registerNewObjective(ChatColor.stripColor(this.title), "dummy", this.title);
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(this.title);

        for(String entry : this.bukkitScoreboard.getEntries()) {
            this.bukkitScoreboard.resetScores(entry);
        }

        for(ScoreboardTeam team : this.teams) {
            team.update();
        }
    }

    /**
     * sets the title of this scoreboard content and updates it
     * @param title the title
     */
    public void setTitle(@NotNull String title) {
        this.title = title;

        update();
    }

    /**
     * adds the scoreboard team specified to this scoreboard content
     * @param team the scoreboard team
     */
    public void add(@NotNull ScoreboardTeam team) {
        if(this.teams.contains(team)) {
            return;
        }

        this.teams.add(team);

        update();
    }

    /**
     * removes the scoreboard team specified from this scoreboard content
     * @param team the scoreboard team
     */
    public void remove(@NotNull ScoreboardTeam team) {
        if(!this.teams.contains(team)) {
            return;
        }

        this.teams.remove(team);

        update();
    }
}
