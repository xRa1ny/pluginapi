package me.xra1ny.pluginapi.models.scoreboard;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
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
    /**
     * the scoreboard content of this global scoreboard
     */
    @Getter(onMethod = @__(@NotNull))
    private final ScoreboardContent scoreboardContent;

    /**
     * the lines of this global scoreboard
     */
    @Getter(onMethod = @__(@NotNull))
    private List<String> lines;

    /**
     * the users of this global scoreboard
     */
    @Getter(onMethod = @__({@NotNull, @Unmodifiable}))
    private final List<RUser> users = new ArrayList<>();

    @SneakyThrows
    public GlobalScoreboard(@NotNull String title, @NotNull String... lines) {
        this.scoreboardContent = new ScoreboardContent(title);
        this.lines = Arrays.asList(lines);

        RPlugin.getInstance().getScoreboardManager().register(this);
    }

    /**
     * sets the lines of this global scoreboard
     * @param lines the lines
     */
    public void setLines(@NotNull String... lines) {
        this.lines = List.of(lines);
    }

    /**
     * updates the scoreboard for the user specified
     * @param user the user
     */
    private void update(@NotNull RUser user) {
        user.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        user.getPlayer().setScoreboard(this.scoreboardContent.getBukkitScoreboard());
    }

    /**
     * updates the scoreboards of all users
     */
    public void update() {
        updateContent();

        for(RUser user : this.users) {
            update(user);
        }
    }

    /**
     * updates the content of this scoreboard
     */
    public void updateContent() {
        this.scoreboardContent.update();

        final Objective objective = this.scoreboardContent.getBukkitScoreboard().getObjective(ChatColor.stripColor(this.scoreboardContent.getTitle()));

        for(int i = 0; i < this.lines.size(); i++) {
            final Score score = objective.getScore(this.lines.get(i) + String.valueOf(ChatColor.RESET).repeat(i));

            score.setScore(this.lines.size()-i);
        }
    }

    /**
     * adds the user specified to this global scoreboard
     * @param user the user
     */
    public void add(@NotNull RUser user) {
        if(this.users.contains(user)) {
            return;
        }

        this.users.add(user);
        update();
    }

    /**
     * removes the user specified from this global scoreboard
     * @param user the user
     */
    public void remove(@NotNull RUser user) {
        if(!this.users.contains(user)) {
            return;
        }

        this.users.remove(user);
        user.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        update();
    }
}
