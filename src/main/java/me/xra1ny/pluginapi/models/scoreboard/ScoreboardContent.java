package me.xra1ny.pluginapi.models.scoreboard;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
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
    @Getter(onMethod = @__(@NotNull))
    private String title;

    @Getter(onMethod = @__(@NotNull))
    private final Scoreboard bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    @Getter(onMethod = @__(@NotNull))
    private final List<ScoreboardTeam> teams = new ArrayList<>();

    ScoreboardContent(@NotNull String title) {
        this.title = title;
    }

    /** Updates this Scoreboards Content */
    public void update() {
        Objective objective = bukkitScoreboard.getObjective(ChatColor.stripColor(title));

        if(objective == null) {
            objective = bukkitScoreboard.registerNewObjective(ChatColor.stripColor(title), "dummy", Component.text(title));
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.displayName(Component.text(title));

        for(String entry : bukkitScoreboard.getEntries()) {
            bukkitScoreboard.resetScores(entry);
        }

        for(ScoreboardTeam team : teams) {
            team.update();
        }
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
        update();
    }

    public void add(@NotNull ScoreboardTeam team) {
        if(teams.contains(team)) {
            return;
        }

        teams.add(team);
        update();
    }

    public void remove(@NotNull ScoreboardTeam team) {
        if(!teams.contains(team)) {
            return;
        }

        teams.remove(team);
        update();
    }
}
