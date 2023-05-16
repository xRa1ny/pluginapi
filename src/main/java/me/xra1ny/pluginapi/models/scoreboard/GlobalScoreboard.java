package me.xra1ny.pluginapi.models.scoreboard;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.models.user.RUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public final class GlobalScoreboard extends RScoreboard {
    @Getter(onMethod = @__(@NotNull))
    @NonNull
    private final ScoreboardContent scoreboardContent;

    @Getter(onMethod = @__(@NotNull))
    private List<String> lines;

    @Getter(onMethod = @__({@NotNull, @Unmodifiable}))
    private final List<RUser> users = new ArrayList<>();

    public GlobalScoreboard(@NotNull String title, @NotNull String... lines) {
        this.scoreboardContent = new ScoreboardContent(title);
        this.lines = Arrays.asList(lines);
    }

    public void setLines(@NotNull String... lines) {
        this.lines = List.of(lines);
    }

    /** Updates the Scoreboard for the User specified */
    private void update(@NotNull RUser user) {
        user.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        user.getPlayer().setScoreboard(this.scoreboardContent.getBukkitScoreboard());
    }

    /** Updates the Scoreboards of all Users */
    public void update() {
        updateContent();

        for(RUser user : this.users) {
            update(user);
        }
    }

    /** Updates the Content of this Scoreboard */
    public void updateContent() {
        this.scoreboardContent.update();

        final Objective objective = this.scoreboardContent.getBukkitScoreboard().getObjective(ChatColor.stripColor(this.scoreboardContent.getTitle()));

        for(int i = 0; i < this.lines.size(); i++) {
            StringBuilder line = new StringBuilder(this.lines.get(i));

            for(int j = 0; j < i; j++) {
                line.append(ChatColor.RESET);
            }

            final Score score = objective.getScore(line.toString());
            score.setScore(this.lines.size()-i);
        }
    }

    public void add(@NotNull RUser user) {
        if(this.users.contains(user)) {
            return;
        }

        this.users.add(user);
        update();
    }

    public void remove(@NotNull RUser user) {
        if(!this.users.contains(user)) {
            return;
        }

        this.users.remove(user);
        user.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        update();
    }
}
