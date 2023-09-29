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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

@Slf4j
public final class PerPlayerScoreboard extends RScoreboard {
    /**
     * the title of this per player scoreboard
     */
    @Getter(onMethod = @__(@NotNull))
    private final String title;

    /**
     * the lines of this per player scoreboard
     */
    @Getter(onMethod = @__(@NotNull))
    private List<Function<RUser, String>> lines;

    /**
     * the scoreboard contents for each member of this per player scoreboard
     */
    @Getter(onMethod = @__(@NotNull))
    private final Map<RUser, ScoreboardContent> scoreboards = new HashMap<>();

    @SneakyThrows
    @SafeVarargs
    public PerPlayerScoreboard(@NotNull String title, @NotNull Function<RUser, String>... lines) {
        this.title = title;
        this.lines = Arrays.asList(lines);

        RPlugin.getInstance().getScoreboardManager().register(this);
    }

    /**
     * sets the lines of this per player scoreboard
     * @param lines the lines
     */
    @SafeVarargs
    public final void setLines(@NotNull Function<RUser, String>... lines) {
        this.lines = List.of(lines);
    }

    /**
     * updates the users specified scoreboard
     * @param user the user
     */
    public void update(@NotNull RUser user) {
        if(!this.scoreboards.containsKey(user)) {
            return;
        }

        user.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        updateContent(user);

        final ScoreboardContent scoreboard = this.scoreboards.get(user);

        user.getPlayer().setScoreboard(scoreboard.getBukkitScoreboard());
    }

    private void updateContent(@NotNull RUser user) {
        if(!this.scoreboards.containsKey(user)) {
            return;
        }

        final ScoreboardContent scoreboard = this.scoreboards.get(user);

        scoreboard.update();

        final Objective objective = scoreboard.getBukkitScoreboard().getObjective(ChatColor.stripColor(scoreboard.getTitle()));
        final List<String> lines = applyLines(user);

        for(int i = 0; i < lines.size(); i++) {
            final Score score = objective.getScore(lines.get(i) + String.valueOf(ChatColor.RESET).repeat(i));

            score.setScore(lines.size()-i);
        }
    }

    /**
     * adds the user specified to this per player scoreboard
     * @param user the user
     */
    public void add(@NotNull RUser user) {
        if(this.scoreboards.containsKey(user)) {
            return;
        }

        this.scoreboards.put(user, new ScoreboardContent(title));
        update(user);
    }

    /**
     * removes the user specified from this per player scoreboard
     * @param user the user
     */
    public void remove(@NotNull RUser user) {
        if(!this.scoreboards.containsKey(user)) {
            return;
        }

        this.scoreboards.remove(user);
        user.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    @NotNull
    private List<String> applyLines(@NotNull RUser user) {
        final List<String> lines = new ArrayList<>();

        for(Function<RUser, String> line : this.lines) {
            lines.add(line.apply(user));
        }

        return lines;
    }

    /**
     * retrieves the scoreboard content of the user specified
     * @param user the user
     * @return the scoreboard content of the user specified
     */
    @Nullable
    public ScoreboardContent getContent(@NotNull RUser user) {
        if(!this.scoreboards.containsKey(user)) {
            return null;
        }

        return this.scoreboards.get(user);
    }
}
